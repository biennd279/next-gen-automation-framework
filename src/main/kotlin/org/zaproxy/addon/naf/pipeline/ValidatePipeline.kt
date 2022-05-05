package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.flow.update
import me.d3s34.rfi.RfiExploiter
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.database.NafDatabase
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.addon.naf.model.RfiRequest
import org.zaproxy.addon.naf.model.mapToIssue
import org.zaproxy.addon.naf.model.toNafAlert
import org.zaproxy.zap.extension.alert.ExtensionAlert
import java.net.URL
import kotlin.coroutines.CoroutineContext

class ValidatePipeline(
    val nafState: NafState,
    override val coroutineContext: CoroutineContext
): NafPipeline<Unit>(NafPhase.ATTACK) {

    private val rfiExploiter = RfiExploiter()

    private val extensionAlert by lazy {
        extensionLoader
            .getExtension(ExtensionAlert::class.java)
    }

    private val nafDatabase by lazy {
        NafDatabase()
    }

    override suspend fun start(nafScanContext: NafScanContext) {

        val alerts = extensionAlert.allAlerts
        val nafAlerts = nafState.alerts.value

        alerts.forEach {alert ->
            val historyReference = alert.historyRef

            when (alert.cweId) {
                89 -> {
                    // SQL Injection
                }
                94, 78, 97 -> {
                    // Code injection, template injection
                }
                98 -> {
                    val isValid = rfiExploiter.validate(
                        RfiRequest(
                            URL(historyReference.uri.toString()),
                            alert.param,
                            data = alert.postData,
                            cookie = historyReference.httpMessage.cookieParamsAsString,
                            remoteFileInclude = RfiExploiter.nessusRfiCheck
                        )
                    )

                    if (isValid) {

                        val nafAlert = nafAlerts.firstOrNull { it.id == alert.alertId.toString() }

                        if (nafAlert != null) {
                            nafDatabase
                                .issueService
                                .saveNewIssue(nafAlert.mapToIssue())
                        } else {

                            val newNafAlert = alert.toNafAlert()

                            nafState.alerts.update {
                                it + newNafAlert
                            }

                            nafDatabase
                                .issueService
                                .saveNewIssue(newNafAlert.mapToIssue())
                        }
                    }
                }
                22 -> {
                    // Path traversal, LFI
                    // No need validate
                    val nafAlert = nafAlerts.firstOrNull { it.id == alert.alertId.toString() }
                    if (nafAlert != null) {
                        nafDatabase
                            .issueService
                            .saveNewIssue(nafAlert.mapToIssue())
                    }
                }
                else -> {}
            }
        }
    }
}