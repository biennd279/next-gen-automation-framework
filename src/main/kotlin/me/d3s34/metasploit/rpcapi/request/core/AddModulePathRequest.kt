package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class AddModulePathRequest(
    val token: String,
    val path: String
): CoreModuleRequest("add_module_path")
