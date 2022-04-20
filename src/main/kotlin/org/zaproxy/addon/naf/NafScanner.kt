package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.pipeline.DetectTargetPipeline
import org.zaproxy.addon.naf.pipeline.NafPipeline
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NafScanner(
    val scanTemplate: ScanTemplate,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): CoroutineScope {

    lateinit var listPipeline: List<NafPipeline<Any, Any>>
    lateinit var target: org.zaproxy.zap.model.Target

    private suspend fun parseScanTemplate(scanTemplate: ScanTemplate) {
        target = detectTarget(scanTemplate.url)
    }

    private suspend fun detectTarget(url: String): org.zaproxy.zap.model.Target {
        val detectTargetPipeline = DetectTargetPipeline(coroutineContext)
        return detectTargetPipeline.start(
            URL(url)
        )
    }
    suspend fun start() {
        parseScanTemplate(scanTemplate)
    }
}