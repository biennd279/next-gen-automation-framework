package me.d3s34.metasploit.rpcapi.request.console

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
abstract class ConsoleModuleRequest(
    override val method: String
): MsfRpcRequest() {
    override val group: String = "console"
}
