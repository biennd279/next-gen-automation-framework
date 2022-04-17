package me.d3s34.metasploit.rpcapi.request.core

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
abstract class CoreModuleRequest(
    override val method: String
): MsfRpcRequest() {
    override val group: String
        get() = "core"
}
