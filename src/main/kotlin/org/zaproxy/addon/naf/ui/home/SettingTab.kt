package org.zaproxy.addon.naf.ui.home

import org.zaproxy.addon.naf.ui.Tab

enum class SettingTab(
    override val title: String
): Tab {
    NUCLEI("Nuclei"), SQLMAP("Sqlmap"), METASPLOIT("Metasploit")
}