package org.zaproxy.addon.naf.component

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import org.zaproxy.addon.naf.model.ActiveScanOptions
import org.zaproxy.addon.naf.model.CrawlOptions
import org.zaproxy.addon.naf.model.ScanTemplate

class WizardComponent(
    componentContext: ComponentContext,
    val onCancel: () -> Unit,
    val onWizardStart: (ScanTemplate) -> Unit
): ComponentContext by componentContext {
    val url = mutableStateOf("")
    val crawlSiteMap = mutableStateOf(true)
    val crawlAjax = mutableStateOf(true)
    val activeScan = mutableStateOf(true)

    private fun buildTemplate(): ScanTemplate {
        return ScanTemplate(
            url = url.value,
            crawlOptions = CrawlOptions(
                crawl = crawlSiteMap.value,
                ajaxCrawl = crawlAjax.value
            ),
            scanOptions = ActiveScanOptions(
                activeScan = activeScan.value
            )
        )
    }

    fun startScan() {
        val template = buildTemplate()
        onWizardStart.invoke(template)
    }
}
