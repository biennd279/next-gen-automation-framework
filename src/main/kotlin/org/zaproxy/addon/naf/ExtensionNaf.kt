package org.zaproxy.addon.naf

import androidx.compose.ui.awt.ComposePanel
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.d3s34.lib.dsl.abstractPanel
import org.apache.logging.log4j.LogManager
import org.parosproxy.paros.Constant
import org.parosproxy.paros.control.Control
import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.extension.ExtensionLoader
import org.parosproxy.paros.extension.history.ExtensionHistory
import org.parosproxy.paros.model.HistoryReference
import org.parosproxy.paros.model.HistoryReferenceEventPublisher
import org.parosproxy.paros.model.SiteMapEventPublisher
import org.zaproxy.addon.naf.component.RootComponent
import org.zaproxy.addon.naf.database.NafDatabase
import org.zaproxy.addon.naf.model.NafAlert
import org.zaproxy.addon.naf.model.NafNode
import org.zaproxy.addon.naf.ui.Root
import org.zaproxy.zap.ZAP
import org.zaproxy.zap.extension.alert.AlertEventPublisher
import org.zaproxy.zap.extension.alert.ExtensionAlert
import org.zaproxy.zap.extension.ascan.ActiveScanEventPublisher
import org.zaproxy.zap.extension.ascan.ExtensionActiveScan
import org.zaproxy.zap.extension.ascan.ScanPolicy
import org.zaproxy.zap.extension.spider.ExtensionSpider
import org.zaproxy.zap.extension.spider.SpiderEventPublisher
import java.awt.BorderLayout
import javax.swing.ImageIcon
import javax.swing.SwingUtilities
import kotlin.coroutines.CoroutineContext

class ExtensionNaf: ExtensionAdaptor(NAME), CoroutineScope, NafState {

    private val exceptionHandler = CoroutineExceptionHandler { context, cause ->
        println("Has exception $cause on $context ")
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.Default + exceptionHandler

    init {
        i18nPrefix = PREFIX
    }

    private val extensionLoader: ExtensionLoader = Control
        .getSingleton()
        .extensionLoader

    lateinit var extHistory: ExtensionHistory

    lateinit var extActiveScan: ExtensionActiveScan

    lateinit var extAlert: ExtensionAlert

    lateinit var extSpider: ExtensionSpider

    lateinit var defaultPolicy: ScanPolicy

    lateinit var database: NafDatabase

    override val getHistoryReference: (Int) -> HistoryReference = {
        extHistory.getHistoryReference(it)
    }

    override val alerts: MutableStateFlow<List<NafAlert>> = MutableStateFlow(emptyList())

    override val historyRefSate: MutableStateFlow<List<HistoryReference>> = MutableStateFlow(emptyList())

    override val siteNodes: MutableStateFlow<List<NafNode>> = MutableStateFlow(emptyList())

    override fun removeAlert(nafAlert: NafAlert) {
        alerts.update {
            it.filter { it.id != nafAlert.id }
        }

        extAlert
            .allAlerts
            .firstOrNull { it.alertId.toString() == nafAlert.id }
            ?.let {
                extAlert.deleteAlert(it)
            }
    }

    private val eventsBus = ZAP.getEventBus()!!

    private val eventConsumerImpl = EventConsumerImpl(this)

    override fun getDescription(): String = Constant.messages.getString("$PREFIX.desc")

    override fun init() {
        // Listen info via Event Bus
        eventsBus.registerConsumer(eventConsumerImpl, AlertEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(eventConsumerImpl, HistoryReferenceEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(eventConsumerImpl, SiteMapEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(eventConsumerImpl, SpiderEventPublisher.getPublisher().publisherName)
        eventsBus.registerConsumer(eventConsumerImpl, ActiveScanEventPublisher.getPublisher().publisherName)

        extHistory = extensionLoader.getExtension(ExtensionHistory::class.java)
        extActiveScan = extensionLoader.getExtension(ExtensionActiveScan::class.java)
        extAlert = extensionLoader.getExtension(ExtensionAlert::class.java)
        extSpider = extensionLoader.getExtension(ExtensionSpider::class.java)

        kotlin.runCatching {
            val policyManager = extActiveScan.policyManager

            defaultPolicy = policyManager
                .templatePolicy

            defaultPolicy.name = "NAF"

            policyManager.savePolicy(defaultPolicy)
        }.onFailure {
            println("Save error $it")
        }

        database = NafDatabase()

        database.connectAndMigrate()
    }
    override fun hook(extensionHook: ExtensionHook): Unit = with(extensionHook) {
        super.hook(this)

        // Hook API
        val api = NafApi(PREFIX)
        addApiImplementor(api)

        addProxyListener(ProxyListenerImpl)

        addConnectionRequestProxyListener(ProxyListenerImpl)
        val nafConfig = MutableStateFlow(database.loadConfig())

        view?.let {
            SwingUtilities.invokeLater {

                val nafService = NafServiceImpl(nafConfig, coroutineContext) {
                    database.saveConfig(nafConfig = nafConfig.value)
                }

                val nafScanner = NafScanner(nafService, defaultPolicy, eventConsumerImpl.nafState, coroutineContext)

                val lifecycle = LifecycleRegistry()
                val rootComponent = RootComponent(
                    componentContext = DefaultComponentContext(lifecycle),
                    nafScanner = nafScanner,
                    nafState =  this@ExtensionNaf,
                    nafDatabase = database,
                    coroutineContext = coroutineContext
                )

                val composePanel = ComposePanel()
                composePanel.setContent {
                    Root(rootComponent)
                }

                hookView.addWorkPanel(abstractPanel {
                    layout = BorderLayout()
                    name = "Nextgen Automation"
                    icon = ICON
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
    
    override fun destroy() {
        super.destroy()
    }

    companion object {
        const val NAME = "Nextgen-automation-framework"
        const val PREFIX = "naf"
        const val RESOURCES = "resources"
        private val ICON = ImageIcon(ExtensionNaf::class.java.getResource("$RESOURCES/cake.png"))
        private val LOGGER = LogManager.getLogger(ExtensionNaf::class.java)!!
    }
}
