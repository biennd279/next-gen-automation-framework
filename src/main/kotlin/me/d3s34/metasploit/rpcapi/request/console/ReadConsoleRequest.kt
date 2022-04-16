package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class ReadConsoleRequest(
    val token: String,
    val consoleId: String
): ConsoleModuleRequest("read")
