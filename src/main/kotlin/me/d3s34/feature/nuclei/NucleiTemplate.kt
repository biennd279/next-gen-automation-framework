package me.d3s34.feature.nuclei

import java.io.File

sealed class NucleiTemplate(
    open val path: String
) {}

data class NucleiTemplateFile(
    override val path: String
) : NucleiTemplate(path)

data class NucleiTemplateDir(
    override val path: String
) : NucleiTemplate(path) {
    fun getTemplates(): List<NucleiTemplate> {
        return File(path)
            .walk()
            .maxDepth(1)
            .filter { it.isDirectory || it.name.endsWith("yaml") || it.name.endsWith("yml") }
            .map {
                if (it.isDirectory) {
                    NucleiTemplateDir(it.path)
                } else {
                    NucleiTemplateFile(it.path)
                }
            }
            .drop(1)
            .toList()
    }
}
