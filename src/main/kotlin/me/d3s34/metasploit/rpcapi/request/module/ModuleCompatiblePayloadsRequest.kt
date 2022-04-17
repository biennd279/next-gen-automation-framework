package me.d3s34.metasploit.rpcapi.request.module

@kotlinx.serialization.Serializable
data class ModuleCompatiblePayloadsRequest(
    val token: String,
    val moduleName: String
): ModuleRequest("compatible_payloads")
