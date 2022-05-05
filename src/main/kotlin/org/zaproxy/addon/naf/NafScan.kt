package org.zaproxy.addon.naf

import androidx.compose.runtime.toMutableStateMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.addon.naf.pipeline.*

class NafScan(
    val listPipeline: MutableList<NafPipeline<*>>,
    var nafScanContext: NafScanContext
) {

    val pipelineState = listPipeline
            .map { Pair(it, PipelineState.NOT_STARTED) }
            .toMutableStateMap()

    private val _phase = MutableStateFlow(NafPhase.INIT)
    val phase: StateFlow<NafPhase> = _phase

    suspend fun start() {
        listPipeline.sortBy { it.phase.priority }

        listPipeline.forEach {
            runCatching {
                pipelineState[it] = PipelineState.RUNNING
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
                    is ValidatePipeline -> {
                        _phase.value = NafPhase.ATTACK
                        it.start(nafScanContext)
                    }
                }
                pipelineState[it] = PipelineState.DONE
            }.onFailure { t ->
                pipelineState[it] = PipelineState.ERROR
                println(it)
            }
        }
    }

    enum class PipelineState {
        NOT_STARTED, RUNNING, DONE, ERROR
    }
}