package me.d3s34.feature.nuclei
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class NucleiResponse(
    @SerialName("host")
    val host: String,
    @SerialName("info")
    val info: Info,
    @SerialName("matched-at")
    val matchedAt: String,
    @SerialName("matched-line")
    val matchedLine: String? = null,
    @SerialName("matcher-name")
    val matcherName: String? = null,
    @SerialName("matcher-status")
    val matcherStatus: Boolean,
    @SerialName("template")
    val template: String,
    @SerialName("template-id")
    val templateId: String,
    @SerialName("template-url")
    val templateUrl: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("type")
    val type: String,
    @SerialName("extracted-results")
    val extractedResults: List<String>,
)

@Serializable
data class Info(
    @SerialName("author")
    val author: List<String>,
    @SerialName("classification")
    val classification: Classification,
    @SerialName("description")
    val description: String,
    @SerialName("name")
    val name: String,
    @SerialName("reference")
    val reference: List<String>?,
    @SerialName("severity")
    val severity: String,
    @SerialName("tags")
    val tags: List<String>,

)

@Serializable
data class Classification(
    @SerialName("cve-id")
    val cveId: String?,
    @SerialName("cvss-metrics")
    val cvssMetrics: String,
    @SerialName("cwe-id")
    val cweId: List<String>
)

