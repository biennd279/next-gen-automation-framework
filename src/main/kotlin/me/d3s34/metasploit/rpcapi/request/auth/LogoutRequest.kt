package me.d3s34.metasploit.rpcapi.request.auth

@kotlinx.serialization.Serializable
data class LogoutRequest(
    val token: String,
    val logoutToken: String
): AuthModuleRequest("logout")
