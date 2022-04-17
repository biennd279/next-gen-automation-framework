package me.d3s34.metasploit.rpcapi.request.module

import kotlinx.serialization.Transient
import me.d3s34.metasploit.rpcapi.model.MsfModuleType

@kotlinx.serialization.Serializable
data class ModuleListRequest(
    val token: String,
    @Transient
    val module: MsfModuleType = MsfModuleType.FALLBACK
): ModuleRequest(module.toString())