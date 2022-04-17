package me.d3s34.metasploit.rpcapi.model

@kotlinx.serialization.Serializable
data class MsfConsole(
    val id: String,
    val prompt: String,
    val busy: Boolean,
)
