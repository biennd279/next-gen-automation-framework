package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.zaproxy.addon.naf.pipeline.ActiveScanPipeline
import org.zaproxy.addon.naf.pipeline.NafCrawlPipeline
import org.zaproxy.addon.naf.pipeline.NafPhase
import org.zaproxy.addon.naf.pipeline.NafPipeline

class NafScan(
    val listPipeline: MutableList<NafPipeline<*, *>>,
    val target: org.zaproxy.zap.model.Target
) {
    private val _phase = MutableStateFlow(NafPhase.INIT)
    val phase: StateFlow<NafPhase> = _phase

    suspend fun start() {
        listPipeline.sortBy { it.phase.priority }

        listPipeline.forEach {
            runCatching<Unit> {
                when (it) {
                    is NafCrawlPipeline -> {
                        _phase.value = NafPhase.CRAWL
                        it.start(target)
                    }
                    is ActiveScanPipeline -> {
                        _phase.value = NafPhase.SCAN
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