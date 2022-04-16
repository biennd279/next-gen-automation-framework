package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class DestroyConsoleRequest(
    val token: String,
    val consoleId: String
): ConsoleModuleRequest("destroy")
