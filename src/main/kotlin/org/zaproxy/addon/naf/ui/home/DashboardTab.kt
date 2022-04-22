package org.zaproxy.addon.naf.ui.home

import org.zaproxy.addon.naf.ui.Tab

enum class DashboardTab(override val title: String): Tab {
    PROCESS("Processing"), ALERT("Alert"), CRAWL("Crawl"), SITEMAP("Sitemap")
}