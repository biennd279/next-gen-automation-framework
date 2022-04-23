package org.zaproxy.addon.naf.pipeline

import me.d3s34.nuclei.NucleiEngine
import me.d3s34.nuclei.NucleiResponse
import me.d3s34.nuclei.NucleiTemplate
import org.apache.commons.httpclient.URI
import org.parosproxy.paros.core.scanner.AbstractAppPlugin
import org.parosproxy.paros.core.scanner.Alert
import org.parosproxy.paros.core.scanner.Category
import org.parosproxy.paros.core.scanner.Plugin
import org.parosproxy.paros.extension.history.ExtensionHistory
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.network.HttpMessage
import org.zaproxy.zap.extension.alert.ExtensionAlert
import org.zaproxy.zap.network.HttpRequestBody
import org.zaproxy.zap.network.HttpResponseBody
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

class NucleiScanPipeline(
    val nucleiEngine: NucleiEngine,
    val templates: List<NucleiTemplate>,
    override val coroutineContext: CoroutineContext,
    val plugin: AbstractAppPlugin = NucleiPlugin,
    ): NafPipeline<org.zaproxy.zap.model.Target, List<NucleiResponse>>(NafPhase.SCAN), Plugin by plugin {

    private val extensionAlert: ExtensionAlert by lazy {
        extensionLoader
            .getExtension(ExtensionAlert::class.java)
    }

    private val extensionHistory: ExtensionHistory by lazy {
        extensionLoader
            .getExtension(ExtensionHistory::class.java)
    }

    private val session by lazy { model.session!! }

    override suspend fun start(input: org.zaproxy.zap.model.Target): List<NucleiResponse> {

        val list: MutableList<NucleiResponse> = mutableListOf()

        val target = input

        val uri = target
            .startNode
            .historyReference
            .uri

        val alertCount = AtomicInteger(1)

        nucleiEngine.scan(url = uri.toString(), templates = templates) {

            val next = alertCount.getAndIncrement()

            val alertUri = URI("${it.host}/${it.path ?: ""}", true)

            val httpMessage = HttpMessage(alertUri)
                .apply {
                    requestBody = HttpRequestBody(it.request ?: "")
                    responseBody = HttpResponseBody(it.response ?: "")
                    note = "Request $next send by Nuclei"
                }

            val historyReference = HistoryReference(
                session,
                HistoryReference.TYPE_SCANNER,
                httpMessage
            )

            extensionHistory.addHistory(historyReference)

            val alert = Alert(pluginId)
                .apply {
                    name = it.info?.name ?: "Nuclei alert $alertCount"
                    alertId = pluginId + next
                    source = Alert.Source.TOOL
                    risk = Integer.min(it.info?.severity?.ordinal ?: Alert.RISK_LOW, Alert.RISK_HIGH)
                    confidence = Alert.CONFIDENCE_MEDIUM
                    this@apply.uri = alertUri.toString()
                    description = it.info?.description ?: "No Info"
                    cweId = it.info?.classification?.cweId?.firstOrNull().toCweId()
                    historyRef = historyReference
                }

            extensionAlert.alertFound(alert, historyReference)

            list.add(it)
        }

        return list
    }


    companion object {
        const val pluginId = 23000

        object NucleiPlugin: AbstractAppPlugin() {
            override fun getId(): Int = pluginId

            override fun getName(): String = "Nuclei Plugin"

            override fun getDescription(): String = ""

            override fun scan() {}

            override fun getCategory(): Int = Category.MISC

            override fun getSolution(): String = ""

            override fun getReference(): String = ""

        }

        private val regexCweId = "\\d+".toRegex()

        private fun String?.toCweId() = this?.let { regexCweId.find(it)?.value?.toInt() } ?: -1
    }
}
