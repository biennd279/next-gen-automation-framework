package me.d3s34.metasploit.rpcapi.request.module

@kotlinx.serialization.Serializable
data class ModuleTargetCompatiblePayloadsRequest(
    val token: String,
    val moduleName: String,
    val targetId: Int
): ModuleRequest("target_compatible_payloads")
