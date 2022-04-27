package me.d3s34.commix

import me.d3s34.lib.command.Commandline
import me.d3s34.lib.command.buildCommand

data class CommixRequest(
    val url: String,
    val data: String? = null,
    val cookies: String? = null,
    val randomAgent: Boolean = false
)

fun CommixRequest.toCommand(): Commandline {
    return buildCommand {
        path = "commix"
        shortFlag = buildMap {
            put("u", url)
            data?.let { put("d", it) }
        }
        longFlag = buildMap {
            cookies?.let { put("cookie", it) }
            if (randomAgent) {
                put("random-agent", null)
            }

            put("batch", null)
            put("disable-coloring", null)
        }
    }
}

