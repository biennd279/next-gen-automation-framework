package me.d3s34.nuclei

import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessReceiveChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessSendChannel
import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.io.core.readBytes
import kotlinx.serialization.json.Json
import me.d3s34.lib.command.buildCommand
import me.d3s34.lib.process.buildSystemExecutor
import me.d3s34.lib.process.throwOnError
import org.slf4j.LoggerFactory
import java.nio.charset.Charset
import kotlin.coroutines.CoroutineContext


class NucleiNativeEngine(
    path: String,
    override val coroutineContext: CoroutineContext
) : NucleiEngine() {
    private val logger = LoggerFactory.getLogger(NucleiNativeEngine::class.java)

    private val fullPath: String = if (path.startsWith("/")) {
        path
    } else {
        ""
    }

    private val deserializer = NucleiResponse.serializer()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun exec(
        url: String,
        templates: List<NucleiTemplate>,
        responseChannel: ProcessSendChannel
    ) = shell {

        kotlin.runCatching {
            val nucleiCommand = buildCommand {
                path = fullPath
                shortFlag = mapOf(
                    "target" to url,
                    "t" to templates.joinToString(separator = ", ") { it.path },
                    "silent" to null,
                    "json" to null,
//                    "as" to null, // automation scan with tech set
                    "duc" to null, // For scanning disable update, we will check update manual
                    "s" to "info, low, medium, high, critical" // Only collect p1-p5 vulnerability
                )
            }

            val executor = buildSystemExecutor(nucleiCommand)

            pipeline { executor pipe responseChannel }

            executor.process.throwOnError()
        }.also {
            responseChannel.close()
        }
            .getOrThrow()
    }

    private suspend fun resultProcess(
        responseChannel: ProcessReceiveChannel,
        resultChannel: SendChannel<NucleiResponse>
    ) {

        for (response in responseChannel) {
            try {
                val result = Json.decodeFromString(
                    deserializer,
                    response.readBytes().toString(Charset.defaultCharset())
                )
                resultChannel.send(result)
            } catch (_: Throwable) {
            }
        }
        resultChannel.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateTemplate(
        templateDir: NucleiTemplateDir
    ): Unit = withContext(coroutineContext) {
        launch {
            val nucleiCommand = buildCommand {
                path = fullPath
                shortFlag = mapOf(
                    "ud" to templateDir.path,
                )
            }

            shell {
                val executor = buildSystemExecutor(nucleiCommand)
                pipeline { executor forkErr nullout pipe nullout }
                executor.process.throwOnError()
            }
        }
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun scan(
        url: String,
        templates: List<NucleiTemplate>,
    ): List<NucleiResponse> = withContext(coroutineContext) {
        val results = mutableListOf<NucleiResponse>()
        val responseChannel: ProcessChannel = Channel()

        val actors = actor<NucleiResponse> {
            for (result in channel) {
                results.add(result)
            }
        }

        val execJob = launch {
            exec(url, templates, responseChannel)
        }

        val processJob = launch {
            resultProcess(responseChannel, actors)
        }

        joinAll(execJob, processJob)

        return@withContext results
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    override suspend fun scan(
        url: String,
        templates: List<NucleiTemplate>,
        onReceive: suspend (NucleiResponse) -> Unit
    ) {
        val responseChannel: ProcessChannel = Channel()

        val actors = actor<NucleiResponse> {
            for (result in channel) {
                onReceive(result)
            }
        }

        //coroutine only process io stream from process thread
        val execJob = launch(Dispatchers.IO) {
            exec(url, templates, responseChannel)
        }

        val processJob = launch {
            resultProcess(responseChannel, actors)
        }

        joinAll(execJob, processJob)
    }

}
