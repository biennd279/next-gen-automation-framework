package org.zaproxy.addon.naf

import org.parosproxy.paros.core.scanner.Plugin
import org.parosproxy.paros.core.scanner.Plugin.AlertThreshold
import org.parosproxy.paros.core.scanner.PluginFactory
import org.zaproxy.zap.extension.ascan.ScanPolicy

class NafPolicySupport(
    val scanPolicy: ScanPolicy,
    val defaultThreshold: AlertThreshold
) {

    private val pluginFactory: PluginFactory = scanPolicy.pluginFactory

    fun disablePlugin(plugin: Plugin) {
        with(plugin) {
            pluginFactory
                .getDependencies(this)
                .filter { it.isEnabled }
                .forEach {
                    it.isEnabled = false
                    alertThreshold = AlertThreshold.OFF
                }
        }
    }

    fun enablePlugin(plugin: Plugin) {
        with(plugin) {
            val dependencies = this.dependency
            if (dependencies == null || dependencies.isEmpty()) {
                return
            }

            val allPluginDep = ArrayList<Plugin>(dependencies.size)
            if (!pluginFactory.addAllDependencies(this, allPluginDep)) {
                return
            }

            allPluginDep
                .toList()
                .filter { !it.isEnabled }
                .forEach {
                    it.isEnabled = true
                    it.alertThreshold = defaultThreshold
                }
        }
//        scanPolicy.save()
    }
}
