package org.zaproxy.addon.naf.component.tab

class StartTabComponent: ExploitTabComponent("Exploit") {
    override suspend fun exploit() {}

    override suspend fun onClose() {}
}