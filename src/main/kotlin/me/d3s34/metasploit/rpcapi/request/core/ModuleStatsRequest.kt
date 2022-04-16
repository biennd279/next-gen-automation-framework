package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class ModuleStatsRequest(
    val token: String
): CoreModuleRequest("module_stats")
