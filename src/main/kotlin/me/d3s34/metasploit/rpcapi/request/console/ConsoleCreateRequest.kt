package me.d3s34.metasploit.rpcapi.request.console

@kotlinx.serialization.Serializable
data class ConsoleCreateRequest(
    val token: String
): ConsoleModuleRequest("create")
