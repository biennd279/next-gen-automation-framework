package me.d3s34.feature.nuclei

import kotlinx.coroutines.CoroutineScope

abstract class NucleiEngine : CoroutineScope {
    abstract suspend fun updateTemplate(
        templateDir: NucleiTemplateDir
    )

    abstract suspend fun scan(
        url: String,
        template: NucleiTemplate
    ): List<NucleiResponse>

    abstract suspend fun scan(
        url: String,
        template: NucleiTemplate,
        onReceive: suspend (NucleiResponse) -> Unit
    )
}
