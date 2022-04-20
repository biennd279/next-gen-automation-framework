package org.zaproxy.addon.naf.model

import org.zaproxy.zap.extension.alert.AlertEventPublisher

data class NafAlert(
    val name: String,
    val uri: String,
    val param: String,
    val risk: Int,
    val riskString: String,
    val confidence: Int,
    val confidenceString: String,
    val source: Int
)

fun Map<String, String>.toNafAlert(): NafAlert {
    return NafAlert(
        this[AlertEventPublisher.NAME] as String,
        this[AlertEventPublisher.URI] as String,
        this[AlertEventPublisher.PARAM] as String,
        this[AlertEventPublisher.RISK]?.toIntOrNull() ?: 0,
        this[AlertEventPublisher.RISK_STRING] as String,
        this[AlertEventPublisher.CONFIDENCE]?.toIntOrNull() ?: 0,
        this[AlertEventPublisher.CONFIDENCE_STRING] as String,
        this[AlertEventPublisher.SOURCE]?.toIntOrNull() ?: 0,
    )
}
