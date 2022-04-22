package org.zaproxy.addon.naf

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.zaproxy.addon.naf.model.NafPlugin
import org.zaproxy.addon.naf.model.ScanTemplate
import org.zaproxy.addon.naf.model.toAlertThreshold
import org.zaproxy.addon.naf.model.toAttackStrength
import org.zaproxy.addon.naf.pipeline.*
import org.zaproxy.zap.extension.ascan.ScanPolicy
import java.net.URL
import kotlin.coroutines.CoroutineContext

class NafScanner(
    val defaultPolicy: ScanPolicy,
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
                val nafPluginMap = scanOptions.plugins.associateBy { it.id }
                val policy = defaultPolicy
                val nafPolicySupport = NafPolicySupport(policy, policy.defaultThreshold)

                policy.pluginFactory!!
                    .allPlugin
                    .forEach { plugin ->
                        nafPluginMap[plugin.id]?.let { nafPlugin ->
                            plugin.alertThreshold = nafPlugin.threshold.toAlertThreshold()
                            plugin.attackStrength = nafPlugin.strength.toAttackStrength()

                            if (nafPlugin.threshold == NafPlugin.Threshold.OFF) {
                                nafPolicySupport.disablePlugin(plugin)
                            } else {
                                nafPolicySupport.enablePlugin(plugin)
                            }
                        }
                    }

                listPipeline.add(ActiveScanPipeline(policy, coroutineContext))

            }
        }

        return NafScan(
            target = target,
            listPipeline = listPipeline
        )
    }
}