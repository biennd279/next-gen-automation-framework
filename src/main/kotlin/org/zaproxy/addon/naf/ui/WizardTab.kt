package org.zaproxy.addon.naf.ui

enum class WizardTab(
    override val title: String
): Tab {
    FUZZ("Fuzz"), CRAWL("Crawl"), SCAN("Scan"), AUTH("Authentication"), SCRIPT("Script")
}
