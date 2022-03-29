package me.d3s34.feature.nuclei

import eu.jrie.jetbrains.kotlinshell.shell.shell
import kotlinx.coroutines.ExperimentalCoroutinesApi

abstract class NucleiEngine {
    abstract fun updateTemplate(templateDir: NucleiTemplateDir)
    abstract fun scan(url: String, template: NucleiTemplate): List<NucleiResponse>
    abstract suspend fun cancel()
}
