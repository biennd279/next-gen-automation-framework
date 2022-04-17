package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class VersionRequest(
    val token: String
): CoreModuleRequest("version")
