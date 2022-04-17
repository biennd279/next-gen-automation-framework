package me.d3s34.metasploit.rpcapi.response.module

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class ModuleListResponse(
    val modules: List<String> = emptyList()
): MsfRpcResponse()
