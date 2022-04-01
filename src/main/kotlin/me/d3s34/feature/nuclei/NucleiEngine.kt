package me.d3s34.feature.nuclei

import kotlinx.coroutines.CoroutineScope

abstract class NucleiEngine : CoroutineScope {
    abstract fun updateTemplate(
        templateDir: NucleiTemplateDir,
        hook: suspend (CoroutineScope) -> Unit = {}
    )

    abstract fun scan(
        url: String,
        template: NucleiTemplate,
        hook: suspend (
            CoroutineScope
        ) -> Unit = {}
    ): List<NucleiResponse>
}
