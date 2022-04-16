package me.d3s34.metasploit.rpcapi.request.core

@kotlinx.serialization.Serializable
data class ThreadListRequest(
    val token: String
): CoreModuleRequest("thread_list")
