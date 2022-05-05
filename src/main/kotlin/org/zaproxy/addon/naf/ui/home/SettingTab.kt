package org.zaproxy.addon.naf.ui.home

import org.zaproxy.addon.naf.ui.Tab

enum class SettingTab(
    override val title: String
): Tab {
     SQLMAP("Sqlmap"), COMMIX("Commix"), TPLMAP("Tplmap"), METASPLOIT("Metasploit"), NUCLEI("Nuclei"),
}