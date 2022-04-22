package org.zaproxy.addon.naf.ui.wizard

import org.zaproxy.addon.naf.ui.Tab

enum class WizardTab(
    override val title: String
): Tab {
    SCOPE("Scope"),
    CRAWL("Crawl"),
    SCAN("Scan"),
    SYSTEM("System"),
    AUTH("Authentication"),
    SCRIPT("Script")
}
