package me.d3s34.metasploit.rpcapi.request.job

@kotlinx.serialization.Serializable
data class JobListRequest(
    val token: String
): JobModuleRequest("list")