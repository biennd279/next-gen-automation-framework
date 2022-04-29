package me.d3s34.metasploit.rpcapi.request.module

@kotlinx.serialization.Serializable
data class ModuleOptionsRequest(
    val token: String,
    val moduleType: String,
    val moduleName: String
): ModuleRequest("options")
