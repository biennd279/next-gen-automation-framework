package me.d3s34.metasploit.rpcapi.response.module

import kotlinx.serialization.SerialName
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse

@kotlinx.serialization.Serializable
data class ModuleExecuteResponse(
    @SerialName("job_id")
    val jobId: String = "",
    val payload: String = ""
): MsfRpcResponse()