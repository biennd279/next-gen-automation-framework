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

@Suppress("Deprecation")
fun NafAlert.mapToIssue(): NafIssue {
    return NafIssue(
        null,
        name = this.name,
        severity = when (this.risk) {
            0 -> Severity.INFO
            1 -> Severity.LOW
            2 -> Severity.MEDIUM
            3 -> Severity.HIGH
            else -> Severity.UNKNOWN
        },
        description = this.description,
        reproduce = """
            Send query to ${this.uri} with ${this.param} is vulnerability value.
        """.trimIndent(),
        solution = this.solution,
        note = this.otherInfo
    )
}
