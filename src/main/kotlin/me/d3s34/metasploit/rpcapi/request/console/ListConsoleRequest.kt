package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class ListConsoleRequest(
    val token: String
): ConsoleModuleRequest("list")
