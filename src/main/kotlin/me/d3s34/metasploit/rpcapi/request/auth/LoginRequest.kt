package me.d3s34.metasploit.rpcapi.request.auth

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
data class LoginRequest(
    val username: String,
    val password: String,
) : MsfRpcRequest() {
    override val group: String = "auth"
    override val method: String = "login"
}
