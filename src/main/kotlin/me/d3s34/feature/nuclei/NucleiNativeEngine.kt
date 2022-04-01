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
    ) {
        for (response in responseChannel) {
            try {
                val result = Json.decodeFromString(
                    deserializer,
                    response.readBytes().toString(Charset.defaultCharset())
                )
                resultChannel.send(result)
            } catch (t: Throwable) {
                logger.warn(
                    "Receive response but can not parsing them ${
                        response.readBytes().toString(Charset.defaultCharset())
                    }"
                )
//                logger.warn(t.message)
            }
        }

        resultChannel.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun updateTemplate(
        templateDir: NucleiTemplateDir,
        hook: suspend (CoroutineScope) -> Unit
    ): Unit = runBlocking {
        withContext(coroutineContext) {

            launch {
                shell {
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

                hook(this)
            }
        }
    }


    @OptIn(ObsoleteCoroutinesApi::class)
    override fun scan(
        url: String,
        template: NucleiTemplate,
        hook: suspend (CoroutineScope) -> Unit
    ): List<NucleiResponse> = runBlocking {
        val results = mutableListOf<NucleiResponse>()

        withContext(coroutineContext) {
            val responseChannel: ProcessChannel = Channel()

            val actors = actor<NucleiResponse> {
                for (result in channel) {
                    results.add(result)
                }
            }

            launch {
                exec(url, template, responseChannel)
            }

            launch {
                resultProcess(responseChannel, actors)
            }

            launch {
                hook(this)
            }
        }

        return@runBlocking results
    }
}
