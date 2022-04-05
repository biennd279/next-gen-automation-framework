package me.d3s34.nuclei

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class NucleiResponse(
    @SerialName("extractor-name")
    val extractorName: String? = null,
    @SerialName("host")
    val host: String? = null,
    @SerialName("info")
    val info: Info? = null,
    @SerialName("matched-at")
    val matchedAt: String? = null,
    @SerialName("matched-line")
    val matchedLine: String? = null,
    @SerialName("matcher-name")
    val matcherName: String? = null,
    @SerialName("matcher-status")
    val matcherStatus: Boolean,
    @SerialName("template")
    val template: String? = null,
    @SerialName("template-id")
    val templateId: String? = null,
    @SerialName("template-url")
    val templateUrl: String? = null,
    @SerialName("timestamp")
    val timestamp: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("extracted-results")
    val extractedResults: List<String>? = null,
    @SerialName("path")
    val path: String? = null,
    @SerialName("request")
    val request: String? = null,
    @SerialName("response")
    val response: String? = null,
    @SerialName("metadata")
    val metadata: Map<String, String>? = null,
    @SerialName("ip")
    val ip: String? = null,
    @SerialName("curl-command")
    val curlCommand: String? = null,
    @SerialName("interaction")
    val interaction: String? = null
)

@Serializable
data class Info(
    @SerialName("author")
    val author: List<String>? = null,
    @SerialName("classification")
    val classification: Classification? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("reference")
    val reference: List<String>? = null,
    @SerialName("severity")
    val severity: Severity = Severity.UNKNOWN,
    @SerialName("tags")
    val tags: List<String>? = null,
    @SerialName("metadata")
    val metadata: Map<String, String>? = null,
    @SerialName("remediation")
    val remediation: String? = null,
)

@Serializable
data class Classification(
    @SerialName("cve-id")
    val cveId: String? = null,
    @SerialName("cvss-metrics")
    val cvssMetrics: String? = null,
    @SerialName("cwe-id")
    val cweId: List<String>? = null,
    @SerialName("cvss-score")
    val cvssScore: Double? = null
)

@Serializable
enum class Severity {
    @SerialName("info")
    INFO,

    @SerialName("low")
    LOW,

    @SerialName("medium")
    MEDIUM,

    @SerialName("high")
    HIGH,

    @SerialName("critical")
    CRITICAL,

    @SerialName("unknown")
    UNKNOWN;

    override fun toString(): String {
        return this.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}

fun main() {
    println(Severity.CRITICAL)
}
