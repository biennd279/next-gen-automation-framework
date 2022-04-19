package org.zaproxy.addon.naf

import androidx.compose.ui.awt.ComposePanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.d3s34.lib.dsl.abstractPanel
import me.d3s34.lib.dsl.jTextPanel
import org.apache.logging.log4j.LogManager
import org.parosproxy.paros.Constant
import org.parosproxy.paros.core.proxy.ConnectRequestProxyListener
import org.parosproxy.paros.core.proxy.ProxyListener
import org.parosproxy.paros.extension.AbstractPanel
import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.extension.history.ProxyListenerLog
import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.parosproxy.paros.network.HttpMessage
import org.zaproxy.addon.naf.ui.Naf
import org.zaproxy.zap.ZAP
import org.zaproxy.zap.eventBus.Event
import org.zaproxy.zap.eventBus.EventConsumer
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.extension.ascan.ActiveScanEventPublisher
import org.zaproxy.zap.extension.spider.SpiderEventPublisher
import org.zaproxy.zap.model.ScanEventPublisher
import org.zaproxy.zap.utils.FontUtils
import java.awt.CardLayout
import java.awt.Font
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import kotlin.coroutines.CoroutineContext

class ExtensionNaf: ExtensionAdaptor(NAME), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    init {
        i18nPrefix = PREFIX
    }

    private val eventsBus = ZAP.getEventBus()!!

    override fun getDescription(): String = Constant.messages.getString("$PREFIX.desc")


    override fun init() {
        eventsBus.registerConsumer(EventConsumerImpl, AlertEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(EventConsumerImpl, HistoryReferenceEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(EventConsumerImpl, SiteMapEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(EventConsumerImpl, SpiderEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(EventConsumerImpl, ActiveScanEventPublisher.getPublisher().publisherName)
    }

    override fun hook(extensionHook: ExtensionHook): Unit = with(extensionHook) {
        super.hook(this)

        // Hook API
        val api = NafApi(PREFIX)
        addApiImplementor(api)

        addProxyListener(ProxyListenerImpl)
        addConnectionRequestProxyListener(ProxyListenerImpl)

        view?.let {
            SwingUtilities.invokeLater {
                val composePanel = ComposePanel()
                composePanel.setContent { Naf() }
                hookView.addWorkPanel(abstractPanel {
                    layout = CardLayout()
                    name = "Workspace panel"
                    add(composePanel)
                }.apply {
                    tabIndex = 0
                })
            }
        }
    }

    companion object {
        const val NAME = "Nextgen-automation-framework"
        const val PREFIX = "naf"
        const val RESOURCES = "resources"
        private val ICON = ImageIcon(ExtensionNaf::class.java.getResource("$RESOURCES/cake.png"))
        private val LOGGER = LogManager.getLogger(ExtensionNaf::class.java)!!
    }
}
