package org.zaproxy.addon.naf.component

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.model.*
import org.zaproxy.zap.extension.ascan.ScanPolicy

class WizardComponent(
    componentContext: ComponentContext,
    val defaultPolicy: ScanPolicy,
    val onCancel: () -> Unit,
    val onWizardStart: (ScanTemplate) -> Unit
): ComponentContext by componentContext {
    val url = mutableStateOf("")
    val crawlSiteMap = mutableStateOf(true)
    val crawlAjax = mutableStateOf(true)
    val activeScan = mutableStateOf(true)

    val nafPlugin: List<MutableState<NafPlugin>> = defaultPolicy
        .pluginFactory
        .allPlugin
        .map {
            mutableStateOf(
                NafPlugin(
                    id = it.id,
                    category = it.category,
                    name = it.name,
                    threshold = it.alertThreshold.toThreshold(),
                    strength = it.attackStrength.toStrength()
                )
            )
        }

    private fun buildTemplate(): ScanTemplate {

        val nafPlugins = nafPlugin.map { it.value }

        return ScanTemplate(
            url = url.value,
            crawlOptions = CrawlOptions(
                crawl = crawlSiteMap.value,
                ajaxCrawl = crawlAjax.value
            ),
            scanOptions = ActiveScanOptions(
                activeScan = activeScan.value,
                plugins = nafPlugins
            )
        )
    }

    fun startScan() {
        val template = buildTemplate()
        onWizardStart.invoke(template)
    }
}
