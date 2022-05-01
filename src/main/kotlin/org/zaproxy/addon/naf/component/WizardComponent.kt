package org.zaproxy.addon.naf.component

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import me.d3s34.nuclei.NucleiTemplate
import me.d3s34.nuclei.NucleiTemplateDir
import org.parosproxy.paros.control.Control
import org.zaproxy.addon.naf.NafScanner
import org.zaproxy.addon.naf.model.*
import org.zaproxy.zap.extension.bruteforce.ExtensionBruteForce
import org.zaproxy.zap.model.Tech
import java.io.File

class WizardComponent(
    componentContext: ComponentContext,
    val nafScanner: NafScanner,
    val onCancel: () -> Unit,
    val onWizardStart: (ScanTemplate) -> Unit
): ComponentContext by componentContext {

    val listBruteForceFile by lazy {
        Control.getSingleton()
            .extensionLoader
            .getExtension(ExtensionBruteForce::class.java)
            .fileList
            .map { it.file }
    }

    val url = mutableStateOf("")
    val crawlSiteMap = mutableStateOf(true)
    val crawlAjax = mutableStateOf(true)
    val activeScan = mutableStateOf(true)
    val includesRegex = mutableStateListOf<String>()
    val exludesRegex = mutableStateListOf<String>()
    val useNuclei = mutableStateOf(true)
    val templates = mutableStateListOf<NucleiTemplate>(
        NucleiTemplateDir(nafScanner.nafService.nucleiRootTemplatePath)
    )
    val includeTech = mutableStateListOf(*Tech.getAll().toTypedArray())
    val excludeTech = mutableStateListOf<Tech>()

    val useBruteForce = mutableStateOf(false)
    val files = mutableStateListOf<File>()

    val nafPlugin: List<MutableState<NafPlugin>> = nafScanner.defaultPolicy
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
            includesRegex = includesRegex,
            excludesRegex = exludesRegex,
            includeTech = includeTech.toSet(),
            excludeTech = excludeTech.toSet(),
            fuzzOptions = FuzzOptions(
                useBruteForce = useBruteForce.value,
                files = files
            ),
            crawlOptions = CrawlOptions(
                crawl = crawlSiteMap.value,
                ajaxCrawl = crawlAjax.value
            ),
            scanOptions = ActiveScanOptions(
                activeScan = activeScan.value,
                plugins = nafPlugins
            ),
            systemOptions = SystemOptions(
                useNuclei = useNuclei.value,
                templates = templates
            )
        )
    }

    fun startScan() {
        val template = buildTemplate()
        onWizardStart.invoke(template)
    }

    companion object {
        fun isValidRegex(regex: String): Boolean {
            return kotlin.runCatching {
                regex.toRegex()
                return true
            }
                .getOrDefault(false)
        }
    }
}
