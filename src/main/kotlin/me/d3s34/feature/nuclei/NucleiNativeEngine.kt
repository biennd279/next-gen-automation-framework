package me.d3s34.feature.nuclei

import eu.jrie.jetbrains.kotlinshell.shell.shell


class NucleiNativeEngine(
    path: String
): NucleiEngine() {
    val fullPath: String = if (path.startsWith("/")) { path } else { "" }

    suspend fun exec(template: NucleiTemplate): List<NucleiResponse> {

        shell {



        }

        return listOf()
    }
}