package me.d3s34.metasploit.rpcapi.response.console

import me.d3s34.metasploit.rpcapi.model.MsfConsole
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class ConsoleListResponse(
    val consoles: List<MsfConsole> = emptyList()
): MsfRpcResponse()
