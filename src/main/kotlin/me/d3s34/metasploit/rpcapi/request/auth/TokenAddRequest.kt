package me.d3s34.metasploit.rpcapi.request.auth

@kotlinx.serialization.Serializable
data class TokenAddRequest(
    val token: String,
    val newToken: String,
): AuthModuleRequest("token_add")
