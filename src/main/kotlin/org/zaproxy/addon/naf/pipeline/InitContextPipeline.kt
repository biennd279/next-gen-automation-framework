package org.zaproxy.addon.naf.pipeline

import org.zaproxy.addon.naf.NafPolicySupport
import org.zaproxy.addon.naf.model.*
import org.zaproxy.zap.extension.ascan.ScanPolicy
import org.zaproxy.zap.model.TechSet
import kotlin.coroutines.CoroutineContext

class InitContextPipeline(
    val scanTemplate: ScanTemplate,
    val defaultPolicy: ScanPolicy,
    override val coroutineContext: CoroutineContext
): NafPipeline<NafScanContext>(NafPhase.INIT) {

    override suspend fun start(nafScanContext: NafScanContext): NafScanContext {

        val session = model.session
        val context = session.getContext("NAF") ?: session.getNewContext("NAF")
        val policy = defaultPolicy
        val nafPolicySupport = NafPolicySupport(policy, policy.defaultThreshold)

        with(scanTemplate) {
            val nafPluginMap = scanOptions.plugins.associateBy { it.id }

            policy.pluginFactory!!
                .allPlugin
                .forEach { plugin ->
                    nafPluginMap[plugin.id]?.let { nafPlugin ->
                        plugin.alertThreshold = nafPlugin.threshold.toAlertThreshold()
                        plugin.attackStrength = nafPlugin.strength.toAttackStrength()

                        if (nafPlugin.threshold == NafPlugin.Threshold.OFF) {
                            nafPolicySupport.disablePlugin(plugin)
                        } else {
                            nafPolicySupport.enablePlugin(plugin)
                        }
                    }
                }

            includesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addIncludeInContextRegex(regex)
                }
            }

            excludesRegex.forEach { regex ->
                kotlin.runCatching {
                    context.addExcludeFromContextRegex(regex)
                    session.addExcludeFromScanRegexs(regex)
                    session.addExcludeFromSpiderRegex(regex)

                    // Hmm... ?
                    // session.addExcludeFromProxyRegex(regex)
                }
            }

            context.techSet = TechSet(includeTech.toTypedArray(), excludeTech.toTypedArray())
        }

        return nafScanContext.copy(context = context, policy = policy)
    }
}