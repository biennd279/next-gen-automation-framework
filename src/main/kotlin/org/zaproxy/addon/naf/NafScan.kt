package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.addon.naf.pipeline.*

class NafScan(
    val listPipeline: MutableList<NafPipeline<*>>,
    var nafScanContext: NafScanContext
) {
    private val _phase = MutableStateFlow(NafPhase.INIT)
    val phase: StateFlow<NafPhase> = _phase

    suspend fun start() {
        listPipeline.sortBy { it.phase.priority }

        listPipeline.forEach {
            runCatching<Unit> {
                when (it) {
                    is InitContextPipeline -> {
                        _phase.value = NafPhase.INIT
                        // Need override context after init
                        nafScanContext = it.start(nafScanContext)
                    }

                    is NafFuzzPipeline -> {
                        _phase.value = NafPhase.FUZZ
                        it.start(nafScanContext)
                    }

                    is NafCrawlPipeline -> {
                        _phase.value = NafPhase.CRAWL
                        it.start(nafScanContext)
                    }

                    is ActiveScanPipeline -> {
                        _phase.value = NafPhase.SCAN
                        it.start(nafScanContext)
                    }

                    is NucleiScanPipeline -> {
                        _phase.value = NafPhase.SCAN
                        it.start(nafScanContext)
                    }
                }
            }
                .onFailure {
                    println(it)
                }
        }
    }
}