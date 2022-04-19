package org.zaproxy.addon.naf.model

data class Issue(
    val name: String,
    val severity: Severity,
    val description: String,
    val reproduce: String,
    val solution: String,
    val note: String
)
