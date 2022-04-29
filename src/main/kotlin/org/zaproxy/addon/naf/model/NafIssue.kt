package org.zaproxy.addon.naf.model

data class NafIssue(
    val id: Int? = null,
    val name: String,
    val severity: Severity,
    val description: String,
    val reproduce: String,
    val solution: String,
    val note: String
)

fun emptyIssue() = NafIssue(
    null,
    "",
    Severity.UNKNOWN,
    "",
    "",
    "",
    ""
)

