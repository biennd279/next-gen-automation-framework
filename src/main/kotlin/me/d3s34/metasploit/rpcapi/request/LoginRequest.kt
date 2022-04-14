package me.d3s34.metasploit.rpcapi.request

@kotlinx.serialization.Serializable
data class Data(
    val data: Int,
    val other: String = "test"
)

@kotlinx.serialization.Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val data: Data
): AbstractRequest() {
    override val group: String = "auth"
    override val method: String = "login"
}
