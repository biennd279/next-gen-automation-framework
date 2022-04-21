package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.parosproxy.paros.model.HistoryReference
import org.zaproxy.zap.extension.spiderAjax.AjaxSpiderParam
import org.zaproxy.zap.extension.spiderAjax.AjaxSpiderTarget
import org.zaproxy.zap.extension.spiderAjax.ExtensionAjax
import org.zaproxy.zap.extension.spiderAjax.SpiderListener
import org.zaproxy.zap.model.Target
import java.net.URI
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

class AjaxSpiderCrawlPipeline(override val coroutineContext: CoroutineContext) : NafCrawlPipeline() {

    val timeRefresh = 500L

    private val extensionAjax: ExtensionAjax? by lazy {
        extensionLoader
            .getExtension(ExtensionAjax::class.java)
    }

    override suspend fun start(input: Target): Set<String> {
        val uri = URI(input.startNode.historyReference.uri.uri)

        extensionAjax?.let {
            val targetBuilder = AjaxSpiderTarget.newBuilder(model.session)
            val options = model.optionsParam.getParamSet(AjaxSpiderParam::class.java).clone()

            val target = targetBuilder
                .setStartUri(uri)
                .setInScopeOnly(false)
                .setInScopeOnly(false)
                .setOptions(options)
                .build()

            launch {
                silentScan(target, it)
            }.join()
        }

        return emptySet()
    }

    suspend fun spiderScan(
        target: AjaxSpiderTarget,
        ajaxExtensionAjax: ExtensionAjax
    ) {
        ajaxExtensionAjax.startScan(target)
    }

    suspend fun silentScan(
        target: AjaxSpiderTarget,
        ajaxExtensionAjax: ExtensionAjax
    ) {
        val threadScan = ajaxExtensionAjax.createSpiderThread(
            "Naf ajax scan", target, object : SpiderListener {
                override fun spiderStarted() {

                }

                override fun foundMessage(
                    historyReference: HistoryReference?,
                    httpMessage: org.parosproxy.paros.network.HttpMessage?,
                    state: SpiderListener.ResourceState?
                ) {

                }

                override fun spiderStopped() {

                }
            }
        )

        val executor = Executors.newSingleThreadExecutor()
        val future = executor.submit(threadScan)

        do {
            delay(timeRefresh)
        } while (isActive && !future.isDone)

        if (!isActive && !future.isDone) {
            future.cancel(true)
        }
    }
}