package me.d3s34.metasploit.rpcapi.request.console

import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class CreateConsoleRequest(
    val token: String
): ConsoleModuleRequest("create")
