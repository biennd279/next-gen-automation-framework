package me.d3s34.metasploit.rpcapi.request.auth

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
abstract class AuthModuleRequest(
    override val method: String
): MsfRpcRequest() {
    override val group: String = "auth"
}