package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.pipeline.*
import org.zaproxy.zap.extension.ascan.ScanPolicy
import kotlin.coroutines.CoroutineContext

class NafScanner(
    val nafService: NafService,
    val defaultPolicy: ScanPolicy,
    val nafState: NafState,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): CoroutineScope {
    private suspend fun detectTarget(url: String): org.zaproxy.zap.model.Target {
        val detectTargetPipeline = DetectTargetPipeline(coroutineContext)
        return detectTargetPipeline.start(url)
    }

    suspend fun parseScanTemplate(scanTemplate: ScanTemplate): NafScan {

        val target = runBlocking { detectTarget(scanTemplate.url) }

        val nafScanContext = NafScanContext(
            startUrl = scanTemplate.url,
            target = target,
            null,
            null
        )

        val listPipeline: MutableList<NafPipeline<*>> = mutableListOf()

        with(scanTemplate) {

            listPipeline.add(InitContextPipeline(scanTemplate, defaultPolicy, coroutineContext))


            if (fuzzOptions.useBruteForce) {
                listPipeline.add(BruteForcePipeline(fuzzOptions.files, coroutineContext))
            }

            if (crawlOptions.crawl) {
                listPipeline.add(SpiderCrawlPipeline(coroutineContext))
            }

            if (crawlOptions.ajaxCrawl) {
                listPipeline.add(AjaxSpiderCrawlPipeline(coroutineContext))
            }

            if (scanOptions.activeScan) {
                listPipeline.add(ActiveScanPipeline(coroutineContext))
            }

            if (systemOptions.useNuclei) {
                nafService.nucleiEngine?.let {
                    listPipeline.add(NucleiScanPipeline(it, systemOptions.templates,coroutineContext))
                }
            }

            if (isValidate) {
                listPipeline.add(ValidatePipeline(
                    nafState = nafState,
                    coroutineContext
                ))
            }
        }

        return NafScan(
            listPipeline = listPipeline,
            nafScanContext
        )
    }
}