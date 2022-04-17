package me.d3s34.metasploit.rpcapi.request.module

import me.d3s34.metasploit.rpcapi.model.MsfModuleType

@kotlinx.serialization.Serializable
data class ModuleInfoRequest(
    val token: String,
    val moduleType: MsfModuleType,
    val moduleName: String
): ModuleRequest("info")
