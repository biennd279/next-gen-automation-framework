package org.zaproxy.addon.naf

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
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
                Panel()
            }

            view?.let {
                hookView.addWorkPanel(abstractPanel {
                    layout = CardLayout()
                    name = "Workspace panel"
                    add(composePanel)
                })
            }
        }

        view?.let {
            hookView.addStatusPanel(statusPanel)

        }
    }

    @Composable
    fun Panel() {
        val count = remember { mutableStateOf(0) }

        Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    count.value++
                }) {
                Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
            }
            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    count.value = 0
                }) {
                Text("Reset")
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