package me.d3s34.metasploit.rpcapi.request

@kotlinx.serialization.Serializable
sealed class AbstractRequest {
    abstract val group: String
    abstract val method: String
}
