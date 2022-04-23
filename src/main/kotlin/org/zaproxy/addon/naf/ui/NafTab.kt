package org.zaproxy.addon.naf.ui

internal interface Tab {
    val title: String
}

enum class NafTab(override val title: String): Tab {
    PROJECT("Project"),
    DASHBOARD("Dashboard"),
    TARGET("Target"),
    ISSUE("Issue"),
    EXPLOIT("Exploit"),
    REPORT("Report"),
    SETTING("Setting")
}
