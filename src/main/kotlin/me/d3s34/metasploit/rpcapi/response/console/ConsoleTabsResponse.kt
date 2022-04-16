package me.d3s34.metasploit.rpcapi.response.console

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class ConsoleTabsResponse(
    val tabs: List<String>
): MsfRpcResponse()

