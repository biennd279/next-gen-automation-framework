package me.d3s34.metasploit.rpcapi.response.auth

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class LoginResponse(
    val token: String = "",
): MsfRpcResponse()
