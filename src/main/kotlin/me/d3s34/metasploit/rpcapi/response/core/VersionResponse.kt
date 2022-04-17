package me.d3s34.metasploit.rpcapi.response.core

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class VersionResponse(
    val version: String,
    val ruby: String,
    val api: String
): MsfRpcResponse()
