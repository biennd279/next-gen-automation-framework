package me.d3s34.metasploit.rpcapi.request.auth

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
data class AddTokenRequest(
    val token: String,
    val newToken: String,
): AuthModuleRequest("add_token")
