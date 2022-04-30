package org.zaproxy.addon.naf.pipeline

import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.zap.model.TechSet
import kotlin.coroutines.CoroutineContext

class InitContextPipeline(
    val scanTemplate: ScanTemplate,
    override val coroutineContext: CoroutineContext
): NafPipeline<org.zaproxy.zap.model.Target, Unit>(NafPhase.INIT) {

    override suspend fun start(input: org.zaproxy.zap.model.Target) {

        val session = model.session
        val context = session.getContext("NAF") ?: session.getNewContext("NAF")

        with(scanTemplate) {
            includesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addIncludeInContextRegex(regex)
                }
            }

            excludesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addExcludeFromContextRegex(regex)
                    session.addExcludeFromScanRegexs(regex)
                    session.addExcludeFromSpiderRegex(regex)

                    // Hmm... ?
                    // session.addExcludeFromProxyRegex(regex)
                }
            }

            context.techSet = TechSet(includeTech.toTypedArray(), excludeTech.toTypedArray())
        }
    }
}