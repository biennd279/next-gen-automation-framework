package me.d3s34.commix

import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessReceiveChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessSendChannel
import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.d3s34.lib.command.buildCommand
import me.d3s34.lib.process.buildSystemExecutor
import me.d3s34.lib.process.throwOnError
import kotlin.coroutines.CoroutineContext

@Deprecated(
    message = "This Engine can not retry all stdout",
    level = DeprecationLevel.WARNING,
)
class CommixNativeEngine(
    val fullPath: String,
    override val coroutineContext: CoroutineContext
) : CommixEngine() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun tryGetShell(
        commixRequest: CommixRequest,
        stdin: ProcessReceiveChannel,
        stdout: ProcessSendChannel
    ) = shell {
            kotlin.runCatching {
                val command = buildCommand {
                    path = fullPath
                    shortFlag = buildMap {
                        put("u", commixRequest.url)
                        commixRequest.data?.let { put("d", it) }
                    }
                    longFlag = buildMap {
                        commixRequest.cookies?.let { put("cookie", it) }
                        if (commixRequest.randomAgent) {
                            put("random-agent", null)
                        }

                        put("batch", null)
                    }
                }

                val executor = buildSystemExecutor(command)

                pipeline { nullin pipe executor pipe stdout }

                executor.process.throwOnError()
            }.also {
                stdout.close()
            }.getOrThrow()
        }
}
