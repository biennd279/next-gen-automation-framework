package me.d3s34.metasploit.rpcapi.request.core

data class ThreadKillRequest(
    val token: String,
    val threadId: String
): CoreModuleRequest("thread_kill")
