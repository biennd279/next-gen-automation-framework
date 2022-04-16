package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class ConsoleWriteRequest(
    val token: String,
    val consoleId: String,
    val input: String
): ConsoleModuleRequest("write")
