package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class ReloadModulesRequest(
    val token: String
): CoreModuleRequest("reload_modules")
