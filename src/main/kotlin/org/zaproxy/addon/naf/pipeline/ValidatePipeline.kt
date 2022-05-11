package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.flow.update
import me.d3s34.commix.CommixValidateRequest
import me.d3s34.rfi.RfiExploiter
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.transformParam
import me.d3s34.tplmap.TplmapRequest
import org.zaproxy.addon.naf.NafService
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
    val nafService: NafService,
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
                    val sqlmapEngine = nafService.sqlmapEngine
                    val isValid = sqlmapEngine?.verifySqlInjection(
                        StartTaskRequest(
                            url = alert.uri.toString(),
                            data = alert.postData,
                            cookie = historyReference.httpMessage.cookieParamsAsString,
                        ).transformParam(alert.param)
                    ) ?: false

                    if (isValid) {
                        nafDatabase
                            .issueService
                            .saveNewIssue(alert.toNafAlert().mapToIssue())
                    }
                }
                94, 78, 97 -> {
                    val commixEngine = nafService.commixDockerEngine

                    val isCommixExploitable = commixEngine?.validate(CommixValidateRequest(
                        url = alert.uri.toString(),
                        data = alert.postData,
                        cookies = historyReference.httpMessage.cookieParamsAsString
                    )) ?: false

                    if (isCommixExploitable) {
                        nafDatabase
                            .issueService
                            .saveNewIssue(alert.toNafAlert().mapToIssue())
                        return@forEach
                    }

                    val tplmapDockerEngine = nafService.tplmapDockerEngine
                    val isTplmapExploitable = tplmapDockerEngine?.validate(
                        TplmapRequest(
                            url = alert.uri.toString(),
                            data = alert.postData,
                            cookies = historyReference.httpMessage.cookieParamsAsString
                        )
                    ) ?: false


                    if (isTplmapExploitable) {
                        nafDatabase
                            .issueService
                            .saveNewIssue(alert.toNafAlert().mapToIssue())
                        return@forEach
                    }
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