package org.zaproxy.addon.naf.pipeline

import org.zaproxy.addon.naf.model.ScanTemplate
import kotlin.coroutines.CoroutineContext

class InitContextPipeline(
    val scanTemplate: ScanTemplate,
    override val coroutineContext: CoroutineContext
): NafPipeline<Nothing?, Unit>(NafPhase.INIT) {

    override suspend fun start(input: Nothing?) {
        val session = model.session

        val context = session.getNewContext("NAF")

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
        }
    }
}