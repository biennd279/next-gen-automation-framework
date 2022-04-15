package me.d3s34.metasploit.rpcapi.response

@kotlinx.serialization.Serializable
data class LoginResponse(
    val result: String? = "",
    val token: String? = "",
): MsfRpcResponse()
