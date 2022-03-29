package me.d3s34.feature.nuclei

import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.json.Json
import me.d3s34.lib.command.buildCommand
import org.slf4j.LoggerFactory


class NucleiNativeEngine(
    path: String
) : NucleiEngine() {
    private val logger = LoggerFactory.getLogger(NucleiNativeEngine::class.java)

    private val fullPath: String = if (path.startsWith("/")) {
        path
    } else {
        ""
    }

    private val deserializer = NucleiResponse.serializer()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun exec(
        url: String,
        template: NucleiTemplate,
        onResponse: suspend (NucleiResponse) -> Unit
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

        pipeline {
            executor pipe stringLambda {
                try {
                    onResponse(Json.decodeFromString(deserializer, it))
                } catch (t: Throwable) {
                    logger.error(t.message)
                }

                return@stringLambda Pair("", "")
            }
        }

        executor.process.throwOnError()
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

    override fun scan(url: String, template: NucleiTemplate): List<NucleiResponse> {
        val result = mutableListOf<NucleiResponse>()

        exec(url, template) {
            result.add(it)
        }

        return result
    }

    override suspend fun cancel() {
        TODO("Not yet implemented")
    }
}
