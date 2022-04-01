package me.d3s34.feature.nuclei

import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessChannelUnit
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessReceiveChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessSendChannel
import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import me.d3s34.lib.command.buildCommand
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext


class NucleiNativeEngine(
    path: String,
    override val coroutineContext: CoroutineContext
) : NucleiEngine(), CoroutineScope{
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

        val executor = systemProcess {
            nucleiCommand.path withArgs nucleiCommand.escapedArgs
        }

        pipeline { executor pipe responseChannel }

        responseChannel.close()
        executor.process.throwOnError()
    }

    private suspend fun resultProcess(
        responseChannel: ProcessReceiveChannel,
        resultChannel: SendChannel<NucleiResponse>
    )  {
        for (response in responseChannel) {
            val result = Json.decodeFromString(deserializer, response.readText())
            resultChannel.send(result)
        }

        resultChannel.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun updateTemplate(
        templateDir: NucleiTemplateDir
    ) = shell {

        val nucleiCommand = buildCommand {
            path = fullPath
            shortFlag = mapOf(
                "ud" to templateDir.path,
            )
        }

        val executor = systemProcess {
            nucleiCommand.path withArgs nucleiCommand.escapedArgs
        }

        pipeline { executor forkErr nullout pipe nullout }

        executor.process.throwOnError()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun scan(url: String, template: NucleiTemplate): List<NucleiResponse> = runBlocking {
        val results = mutableListOf<NucleiResponse>()
        val responseChannel : ProcessChannel = Channel()

        val actors = actor<NucleiResponse>(coroutineContext) {
            for (result in channel) { results.add(result) }
        }

        launch(coroutineContext) {
            exec(url, template, responseChannel)
        }

        launch(coroutineContext) {
            resultProcess(responseChannel, actors)
        }

        return@runBlocking results
    }

    override suspend fun cancel() {
        TODO("Not yet implemented")
    }
}
