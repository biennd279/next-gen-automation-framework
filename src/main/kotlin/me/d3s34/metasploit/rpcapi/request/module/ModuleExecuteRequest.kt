package me.d3s34.metasploit.rpcapi.request.module

import me.d3s34.lib.msgpack.MessagePackSerializer
import me.d3s34.metasploit.rpcapi.model.MsfDatastore
import me.d3s34.metasploit.rpcapi.model.MsfModuleType

@kotlinx.serialization.Serializable()
data class ModuleExecuteRequest(
    val token: String,
    val moduleType: MsfModuleType,
    val moduleName: String,
    val options: MsfDatastore? = null
): ModuleRequest("execute")