package me.d3s34.metasploit.rpcapi.request.module

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
abstract class ModuleRequest(
    override val method: String
): MsfRpcRequest() {
    override val group: String
        get() = "module"
}