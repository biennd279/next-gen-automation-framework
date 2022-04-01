/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.simpleexample

import me.d3s34.lib.dsl.abstractPanel
import me.d3s34.lib.dsl.jTextPanel
import org.apache.logging.log4j.LogManager
import org.parosproxy.paros.Constant
import org.parosproxy.paros.extension.AbstractPanel
import org.parosproxy.paros.extension.ExtensionAdaptor
import org.parosproxy.paros.extension.ExtensionHook
import org.parosproxy.paros.view.View
import org.zaproxy.zap.utils.FontUtils
import org.zaproxy.zap.view.ZapMenuItem
import java.awt.CardLayout
import java.awt.Font
import java.io.File
import java.nio.file.Files
import javax.swing.ImageIcon

/**
 * An example ZAP extension which adds a top level menu item, a pop up menu item and a status panel.
 *
 *
 * [ExtensionAdaptor] classes are the main entry point for adding/loading functionalities
 * provided by the add-ons.
 *
 * @see .hook
 */
class NextgenAutomationFramework : ExtensionAdaptor(NAME) {
    private var menuExample: ZapMenuItem? = null
    private var popupMsgMenuExample: RightClickMsgMenu? = null
        get() {
            if (field == null) {
                field = RightClickMsgMenu(
                    this, Constant.messages.getString("$PREFIX.popup.title")
                )
            }
            return field
        }
    private val statusPanel: AbstractPanel by lazy {
        abstractPanel {
            layout = CardLayout()
            name = Constant.messages.getString("$PREFIX.panel.title")
            icon = ICON

            add {
                jTextPanel {
                    isEditable = false
                    font = FontUtils.getFont("Dialog", Font.PLAIN)
                    contentType = "text/html"
                    text = Constant.messages.getString("$PREFIX.panel.msg")
                }
            }
        }
    }
    private lateinit var api: NextgenAutomationApi

    init {
        i18nPrefix = PREFIX
    }

    override fun hook(extensionHook: ExtensionHook) {
        super.hook(extensionHook)
        api = NextgenAutomationApi()
        extensionHook.addApiImplementor(api)

        // As long as we're not running as a daemon
        if (view != null) {
            extensionHook.hookMenu.addToolsMenuItem(getMenuExample())
            extensionHook.hookMenu.addPopupMenuItem(popupMsgMenuExample)
            extensionHook.hookView.addStatusPanel(statusPanel)
        }
    }

    override fun canUnload(): Boolean {
        // The extension can be dynamically unloaded, all resources used/added can be freed/removed
        // from core.
        return true
    }

    private fun getMenuExample(): ZapMenuItem {
        if (menuExample == null) {
            menuExample = ZapMenuItem("$PREFIX.topmenu.tools.title")
            menuExample!!.addActionListener {
                // This is where you do what you want to do.
                // In this case we'll just show a popup message.
                View.getSingleton()
                    .showMessageDialog(
                        Constant.messages.getString("$PREFIX.topmenu.tools.msg")
                    )
                // And display a file included with the add-on in the Output tab
                displayFile(EXAMPLE_FILE)
            }
        }
        return menuExample!!
    }

    private fun displayFile(file: String) {
        if (!View.isInitialised()) {
            // Running in daemon mode, shouldnt have been called
            return
        }
        try {
            val f = File(Constant.getZapHome(), file)
            if (!f.exists()) {
                // This is something the user should know, so show a warning dialog
                View.getSingleton()
                    .showWarningDialog(
                        Constant.messages.getString(
                            "$PREFIX.error.nofile",
                            f.absolutePath
                        )
                    )
                return
            }
            // Quick way to read a small text file
            val contents = String(Files.readAllBytes(f.toPath()))
            // Write to the output panel
            View.getSingleton().outputPanel.append(contents)
            // Give focus to the Output tab
            View.getSingleton().outputPanel.setTabFocus()
        } catch (e: Exception) {
            // Something unexpected went wrong, write the error to the log
            LOGGER.error(e.message, e)
        }
    }

    override fun getDescription(): String {
        return Constant.messages.getString("$PREFIX.desc")
    }

    companion object {
        // The name is public so that other extensions can access it
        const val NAME = "naf"

        // The i18n prefix, by default the package name - defined in one place to make it easier
        // to copy and change this example
        const val PREFIX = "naf"

        /**
         * Relative path (from add-on package) to load add-on resources.
         *
         * @see Class.getResource
         */
        private const val RESOURCES = "resources"
        private val ICON = ImageIcon(NextgenAutomationFramework::class.java.getResource("$RESOURCES/cake.png"))
        private const val EXAMPLE_FILE = "example/ExampleFile.txt"
        private val LOGGER = LogManager.getLogger(NextgenAutomationFramework::class.java)
    }
}