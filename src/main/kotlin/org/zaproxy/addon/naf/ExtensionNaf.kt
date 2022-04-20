package org.zaproxy.addon.naf

import androidx.compose.ui.awt.ComposePanel
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.d3s34.lib.dsl.abstractPanel
import org.apache.logging.log4j.LogManager
import org.parosproxy.paros.Constant
import org.parosproxy.paros.control.Control
import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.extension.ExtensionLoader
import org.parosproxy.paros.extension.history.ExtensionHistory
import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.addon.naf.component.RootComponent
import org.zaproxy.addon.naf.ui.Root
import org.zaproxy.zap.ZAP
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.extension.alert.ExtensionAlert
import org.zaproxy.zap.extension.ascan.ActiveScanEventPublisher
import org.zaproxy.zap.extension.ascan.ExtensionActiveScan
import org.zaproxy.zap.extension.spider.ExtensionSpider
import org.zaproxy.zap.extension.spider.SpiderEventPublisher
import java.awt.CardLayout
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

    private val extensionLoader: ExtensionLoader by lazy {
        Control
            .getSingleton()
            .extensionLoader
    }

    val extensionHistory: ExtensionHistory by lazy { extensionLoader.getExtension(ExtensionHistory::class.java) }

    val extensionActiveScan: ExtensionActiveScan by lazy { extensionLoader.getExtension(ExtensionActiveScan::class.java) }

    val extensionAlert: ExtensionAlert by lazy { extensionLoader.getExtension(ExtensionAlert::class.java) }

    val extensionSpider: ExtensionSpider by lazy { extensionLoader.getExtension(ExtensionSpider::class.java) }


    override fun hook(extensionHook: ExtensionHook): Unit = with(extensionHook) {
        super.hook(this)

        // Hook API
        val api = NafApi(PREFIX)
        addApiImplementor(api)

        addProxyListener(ProxyListenerImpl)
        addConnectionRequestProxyListener(ProxyListenerImpl)

        view?.let {
            SwingUtilities.invokeLater {
                val lifecycle = LifecycleRegistry()
                val root = RootComponent(
                    DefaultComponentContext(lifecycle)
                )

                val composePanel = ComposePanel()
                composePanel.setContent {
                    Root(root)
                }

                hookView.addWorkPanel(abstractPanel {
                    layout = CardLayout()
                    name = "Workspace panel"
                    add(composePanel)
                }.apply {
                    tabIndex = 0
                    isLocked = true
                    isLocked = true
                    isShowByDefault = true
                    isHideable = false
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
