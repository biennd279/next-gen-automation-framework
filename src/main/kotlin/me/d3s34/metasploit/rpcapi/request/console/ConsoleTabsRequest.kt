package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class ConsoleTabsRequest(
    val token: String,
    val consoleId: String,
    val inputLine: String,
): ConsoleModuleRequest("tabs")
