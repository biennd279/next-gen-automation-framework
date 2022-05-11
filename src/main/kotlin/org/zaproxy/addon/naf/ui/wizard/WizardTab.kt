package org.zaproxy.addon.naf.ui.wizard

import org.zaproxy.addon.naf.ui.Tab

enum class WizardTab(
    override val title: String
): Tab {
    SCOPE("Scope"),
    TECH_SET("Tech"),
    FUZZ("Fuzzing"),
    CRAWL("Crawl"),
    SCAN("Scan"),
    COMPONENT("Component Scan"),
    AUTH("Authentication"),
    SCRIPT("Script")
}
