package me.d3s34.metasploit.rpcapi.response.core

@kotlinx.serialization.Serializable
data class ThreadResponse(
    val status: String = "",
    val critical: Boolean = false,
    val name: String = "",
    val started: String = ""
)
