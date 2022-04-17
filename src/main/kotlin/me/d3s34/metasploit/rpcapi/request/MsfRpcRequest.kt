package me.d3s34.metasploit.rpcapi.request

@kotlinx.serialization.Serializable
abstract class MsfRpcRequest {
    abstract val group: String
    abstract val method: String
}
