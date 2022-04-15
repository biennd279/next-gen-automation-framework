package me.d3s34.metasploit.rpcapi.request

@kotlinx.serialization.Serializable
sealed class MsfRpcRequest {
    abstract val group: String
    abstract val method: String
}
