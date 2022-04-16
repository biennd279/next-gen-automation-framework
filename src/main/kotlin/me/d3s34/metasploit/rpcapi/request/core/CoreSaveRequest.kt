package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class CoreSaveRequest(
    val token: String
): CoreModuleRequest("core_save")
