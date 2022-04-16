package me.d3s34.metasploit.rpcapi.request.auth

@kotlinx.serialization.Serializable
data class TokenRemoveRequest(
    val token: String,
    val tokenRemove: String
): AuthModuleRequest("token_remove")
