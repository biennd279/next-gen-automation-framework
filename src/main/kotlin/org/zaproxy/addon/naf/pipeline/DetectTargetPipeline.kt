package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.apache.commons.httpclient.URI
import org.parosproxy.paros.control.Control
import org.parosproxy.paros.extension.ExtensionLoader
import org.parosproxy.paros.extension.history.ExtensionHistory
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.Model
import org.parosproxy.paros.model.SiteNode
import org.parosproxy.paros.network.HttpMessage
import org.parosproxy.paros.network.HttpSender
import org.parosproxy.paros.network.HttpStatusCode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.zaproxy.zap.model.Target
import javax.swing.SwingUtilities
import kotlin.coroutines.CoroutineContext

class DetectTargetPipeline(
    override val coroutineContext: CoroutineContext
): CoroutineScope {

    val extensionLoader: ExtensionLoader = Control
        .getSingleton()
        .extensionLoader

    val model: Model = Model.getSingleton()

    private val extHistory: ExtensionHistory by lazy {
        extensionLoader
            .getExtension(ExtensionHistory::class.java)
    }

    private val httpSender by lazy {
        HttpSender(
            model.optionsParam.connectionParam,
            true,
            HttpSender.MANUAL_REQUEST_INITIATOR
        )
    }

    suspend fun start(startUrl: String): Target {
        return kotlin.runCatching {
            Target(getStartNode(startUrl))
                .apply {
                    isRecurse = true
                }
        }
            .onFailure {
                logger.error(it.message)
            }
            .getOrThrow()
    }

    private suspend fun getStartNode(url: String): SiteNode {
        val msg = HttpMessage(URI(url, true))
        httpSender.sendAndReceive(msg)

        if (!HttpStatusCode.isSuccess(msg.responseHeader.statusCode)) {
//            throw Throwable("Site return code ${msg.responseHeader.statusCode}")
        }

        extHistory.addHistory(msg, HistoryReference.TYPE_PROXIED)

        withContext(Dispatchers.IO) {
            SwingUtilities.invokeAndWait {
                model.session.siteTree.addPath(msg.historyRef)
            }
        }

        val uri = URI(if (url.endsWith("/")) url.dropLast(1) else url, false)

        repeat(10) {
            val siteNode = model.session.siteTree.findNode(uri)
            if (siteNode != null) {
                return siteNode
            }
            delay(200L)
        }

        throw Throwable("Can not add to history $uri")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }
}