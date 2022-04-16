package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class CoreStopRequest(
    val token: String
): CoreModuleRequest("stop")
