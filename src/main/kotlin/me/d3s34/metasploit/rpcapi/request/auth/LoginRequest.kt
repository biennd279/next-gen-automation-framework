package me.d3s34.metasploit.rpcapi.request.auth

@kotlinx.serialization.Serializable
data class LoginRequest(
    val username: String,
    val password: String,
) : AuthModuleRequest("login")
