package me.d3s34.metasploit.rpcapi.model

@kotlinx.serialization.Serializable
data class MsfThread(
    val id: Int,
    val status: String,
    val critical: Boolean,
    val name: String,
    val started: String
)
