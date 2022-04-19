package org.zaproxy.addon.naf

import androidx.compose.ui.awt.ComposePanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import me.d3s34.lib.dsl.abstractPanel
import me.d3s34.lib.dsl.jTextPanel
import org.apache.logging.log4j.LogManager
import org.parosproxy.paros.Constant
import org.parosproxy.paros.extension.AbstractPanel
import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.zaproxy.addon.naf.ui.Naf
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

    override fun getDescription(): String = Constant.messages.getString("$PREFIX.desc")

    private val statusPanel: AbstractPanel by lazy {
        abstractPanel {
            layout = CardLayout()
            name = Constant.messages.getString("$PREFIX.panel.title")
            icon = ICON

            add(jTextPanel {
                isEditable = false
                font = FontUtils.getFont("Dialog", Font.PLAIN)
                contentType = "text/html"
                text = Constant.messages.getString("$PREFIX.panel.msg") + "Test"
            })
        }
    }

    override fun hook(extensionHook: ExtensionHook): Unit = with(extensionHook) {
        super.hook(this)

        // Hook API
        val api = NafApi(PREFIX)
        addApiImplementor(api)

        SwingUtilities.invokeLater {

            val composePanel = ComposePanel()

            composePanel.setContent {
                Naf()
            }

            view?.let {
                hookView.addWorkPanel(abstractPanel {
                    layout = CardLayout()
                    name = "Workspace panel"
                    add(composePanel)
                }.apply {
                    isVisible = true
                })
            }
        }

        view?.let {
            hookView.addStatusPanel(statusPanel)
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