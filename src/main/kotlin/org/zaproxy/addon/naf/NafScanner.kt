package org.zaproxy.addon.naf

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.parosproxy.paros.model.SiteNode
import org.zaproxy.addon.naf.pipeline.NafCrawlPipeline
import org.zaproxy.addon.naf.pipeline.NafPhase
import org.zaproxy.addon.naf.pipeline.NafPipeline
import kotlin.coroutines.CoroutineContext
import org.zaproxy.zap.model.Target

class NafScanner(
    val siteNode: SiteNode,
    val pipelines: List<NafPipeline<Any, Any>>,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
): CoroutineScope {

    suspend fun startScan() {
        val context = NafContext()

        val target = Target(siteNode)
        target.isRecurse = true

        val pipelineMap = pipelines
            .groupBy { it.phase }
    }
}