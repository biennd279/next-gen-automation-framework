package me.d3s34.metasploit.rpcapi.request

@kotlinx.serialization.Serializable
data class LoginRequest(
    val username: String,
    val password: String,
) : AbstractRequest() {
    override val group: String = "auth"
    override val method: String = "login"
}
