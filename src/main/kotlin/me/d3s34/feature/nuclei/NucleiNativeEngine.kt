package me.d3s34.feature.nuclei

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
        template: NucleiTemplate,
        responseChannel: ProcessSendChannel
    ) = shell {

        val nucleiCommand = buildCommand {
            path = fullPath
            shortFlag = mapOf(
                "target" to url,
                "t" to template.path,
                "silent" to null,
                "json" to null,
            )
        }

        val executor = buildSystemExecutor(nucleiCommand)

        pipeline { executor pipe responseChannel }

        responseChannel.close()
        executor.process.throwOnError()

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


    @OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    override suspend fun scan(
        url: String,
        template: NucleiTemplate,
    ): List<NucleiResponse> = withContext(coroutineContext) {
        val results = mutableListOf<NucleiResponse>()
        val responseChannel: ProcessChannel = Channel()

        val actors = actor<NucleiResponse> {
            for (result in channel) {
                results.add(result)
            }
        }

        val execJob = launch {
            exec(url, template, responseChannel)
        }

        val processJob = launch {
            resultProcess(responseChannel, actors)
        }

        joinAll(execJob, processJob)

        return@withContext results
    }

}
