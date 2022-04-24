package org.zaproxy.addon.naf.model

import org.parosproxy.paros.core.scanner.Alert

data class NafAlert(
    val id: String,
    val name: String,
    val uri: String,
    val param: String,
    val risk: Int,
    val riskString: String,
    val confidence: Int,
    val confidenceString: String,
    val source: Int,
    val cweId: Int,
    val description: String,
    val solution: String,
    val otherInfo: String,
)

fun Alert.toNafAlert(): NafAlert {
    return NafAlert(
        id = this.alertId.toString(),
        name = this.name,
        uri = this.uri,
        param = this.param,
        risk = this.risk,
        riskString = Alert.MSG_RISK[this.risk],
        confidence = this.confidence,
        confidenceString =  Alert.MSG_CONFIDENCE[this.confidence],
        source = this.sourceHistoryId,
        cweId = this.cweId,
        description = this.description,
        otherInfo = this.otherInfo,
        solution = this.solution
    )
}
