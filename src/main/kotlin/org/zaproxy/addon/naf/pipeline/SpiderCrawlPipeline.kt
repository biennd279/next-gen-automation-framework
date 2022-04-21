package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.zaproxy.zap.extension.spider.ExtensionSpider
import org.zaproxy.zap.model.Target
import kotlin.coroutines.CoroutineContext

class SpiderCrawlPipeline(
    override val coroutineContext: CoroutineContext = Dispatchers.Default
) : NafCrawlPipeline() {

    val timeRefresh = 500L


    private val extensionSpider: ExtensionSpider by lazy {
        extensionLoader
            .getExtension(ExtensionSpider::class.java)
    }

    override suspend fun start(input: Target): Set<String> {
        val scanId = kotlin.runCatching {
            extensionSpider.startScan(
                input.displayName,
                input,
                null,
                null
            )
        }
            .onFailure {
                return emptySet()
            }
            .getOrThrow()

        val scan = extensionSpider.getScan(scanId) ?: throw Throwable()

        do {
            delay(timeRefresh)
        } while (isActive && scan.isRunning)

        if (!isActive && scan.isRunning) {
            scan.stopScan()
        }

        return scan.results
    }
}