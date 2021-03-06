package me.d3s34.nuclei

import kotlinx.coroutines.CoroutineScope

abstract class NucleiEngine : CoroutineScope {
    abstract suspend fun updateTemplate(
        templateDir: NucleiTemplateDir
    )

    abstract suspend fun scan(
        url: String,
        templates: List<NucleiTemplate>
    ): List<NucleiResponse>


    abstract suspend fun scan(
        url: String,
        templates: List<NucleiTemplate>,
        onReceive: suspend (NucleiResponse) -> Unit
    )
}
