package me.d3s34.metasploit.rpcapi.request.job

@kotlinx.serialization.Serializable
data class JobStopRequest(
    val token: String,
    val jobId: String
): JobModuleRequest("stop")
