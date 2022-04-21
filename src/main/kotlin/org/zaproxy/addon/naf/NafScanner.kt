package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.pipeline.*
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NafScanner(
    val scanTemplate: ScanTemplate,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): CoroutineScope {

    private val _phase = MutableStateFlow(NafPhase.INIT)
    val phase: StateFlow<NafPhase> = _phase

    lateinit var listPipeline: MutableList<NafPipeline<*, *>>
    lateinit var target: org.zaproxy.zap.model.Target

    private suspend fun parseScanTemplate(scanTemplate: ScanTemplate) {
        target = detectTarget(scanTemplate.url)
        listPipeline = mutableListOf()

        with(scanTemplate) {
            if (crawlOptions.crawl) {
                listPipeline.add(SpiderCrawlPipeline(coroutineContext))
            }

            if (crawlOptions.ajaxCrawl) {
                listPipeline.add(AjaxSpiderCrawlPipeline(coroutineContext))

            }
        }
    }

    private suspend fun detectTarget(url: String): org.zaproxy.zap.model.Target {
        val detectTargetPipeline = DetectTargetPipeline(coroutineContext)
        return detectTargetPipeline.start(
            URL(url)
        )
    }
    suspend fun start() {
        parseScanTemplate(scanTemplate)
        listPipeline.sortBy { it.phase.priority }

        listPipeline.forEach {
            runCatching<Unit> {
                when (it) {
                    is NafCrawlPipeline -> {
                        _phase.value = NafPhase.CRAWL
                        it.start(target)
                    }
                }
            }
                .onFailure {
                    println(it)
                }
        }
    }
}