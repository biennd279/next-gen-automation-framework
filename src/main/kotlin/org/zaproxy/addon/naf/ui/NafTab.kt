package org.zaproxy.addon.naf.ui

interface Tab {
    val title: String
}

enum class NafTab(override val title: String): Tab {
    PROJECT("Project"),
    DASHBOARD("Dashboard"),
    SCANNING("Target"),
    ISSUE("Issue"),
    EXPLOIT("Exploit"),
    REPORT("Report")
}

enum class DashboardTab(override val title: String): Tab {
    ACTIVITY("Activity"), SITEMAP("Sitemap"), REQUEST("Request")
}
