package me.d3s34.metasploit.rpcapi.request.job

import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

@kotlinx.serialization.Serializable
abstract class JobModuleRequest(
    override val method: String
): MsfRpcRequest() {
    override val group: String = "job"
}
