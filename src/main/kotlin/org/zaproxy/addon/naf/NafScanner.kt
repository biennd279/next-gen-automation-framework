package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.pipeline.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NafScanner(
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): CoroutineScope {
    private suspend fun detectTarget(url: String): org.zaproxy.zap.model.Target {
        val detectTargetPipeline = DetectTargetPipeline(coroutineContext)
        return detectTargetPipeline.start(
            URL(url)
        )
    }
     suspend fun parseScanTemplate(scanTemplate: ScanTemplate): NafScan {

        val target = runBlocking { detectTarget(scanTemplate.url) }
        val listPipeline: MutableList<NafPipeline<*, *>> = mutableListOf()

        with(scanTemplate) {
            if (crawlOptions.crawl) {
                listPipeline.add(SpiderCrawlPipeline(coroutineContext))
            }

            if (crawlOptions.ajaxCrawl) {
                listPipeline.add(AjaxSpiderCrawlPipeline(coroutineContext))
            }

            if (scanOptions.activeScan) {
                listPipeline.add(ActiveScanPipeline(coroutineContext))
            }
        }

        return NafScan(
            target = target,
            listPipeline = listPipeline
        )
    }
}