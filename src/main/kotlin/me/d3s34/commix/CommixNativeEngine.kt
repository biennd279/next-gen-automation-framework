package me.d3s34.commix

import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessChannelUnit
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessReceiveChannel
import eu.jrie.jetbrains.kotlinshell.processes.process.ProcessSendChannel
import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import me.d3s34.lib.command.buildCommand
import me.d3s34.lib.process.buildSystemExecutor
import me.d3s34.lib.process.throwOnError
import kotlin.coroutines.CoroutineContext

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

fun main(): Unit = runBlocking {
    val engine = CommixNativeEngine(
        "/home/bien/Downloads/commix-3.4/commix.py",
        Dispatchers.IO + SupervisorJob()
    )

    val stdin = Channel<ProcessChannelUnit>()
    val stdout = Channel<ProcessChannelUnit>()

    val commixRequest = CommixRequest(
        url = "http://localhost:8888/command.php",
        data = "dir=/tmp"
    )

    launch {
        for (output in stdout) {
            println(output.readText())
            System.out.flush()
        }
    }

    val job = launch { engine.tryGetShell(commixRequest, stdin, stdout) }

//    launch {
//        delay(500)
//        while (true) {
//            val input = readLine()
//            input?.let {
//                stdin.send(it.toByteArray())
//            }
//        }
//    }

    job.join()
    stdin.close()
}

