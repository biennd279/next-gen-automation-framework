package me.d3s34.tplmap

import me.d3s34.lib.command.Commandline

data class TplmapRequest(
    val url: String,
    val data: String? = null,
    val cookies: String? = null,
    val osShell: Boolean = false,
    val osCmd: String = "id"
)

fun TplmapRequest.toCommand(): Commandline {
    return Commandline.buildCommand {
        path = "tplmap"
        shortFlag = buildMap {
            put("u", url)
            data?.let {
                put("d", it)
            }
            cookies?.let {
                put("c", it)
            }
        }

        longFlag = if (osShell) {
            mapOf("os-shell" to null)
        } else {
            mapOf("os-cmd" to osCmd)
        }
    }
}
