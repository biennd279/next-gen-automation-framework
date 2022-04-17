package me.d3s34.metasploit.rpcapi.request.module

import me.d3s34.lib.msgpack.MessagePackSerializer
import me.d3s34.metasploit.rpcapi.model.MsfDatastore

@kotlinx.serialization.Serializable()
data class ModuleEncodeRequest(
    val token: String,
    val data: String,
    val encoderModule: String,
    val options: MsfDatastore? = null
): ModuleRequest("encode")
