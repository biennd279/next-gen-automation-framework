package me.d3s34.feature.nuclei

abstract class NucleiEngine {
    abstract fun updateTemplate(templateDir: NucleiTemplateDir)
    abstract fun scan(url: String, template: NucleiTemplate): List<NucleiResponse>
    abstract suspend fun cancel()
}
