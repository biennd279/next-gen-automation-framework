package me.d3s34.metasploit.rpcapi.response.console

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class ConsoleCreateResponse(
    val id: String = "",
    val prompt: String = "",
    val busy: Boolean = false
): MsfRpcResponse()
