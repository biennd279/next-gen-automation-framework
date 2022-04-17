package me.d3s34.metasploit.rpcapi.model

import java.util.*

@Suppress("UNUSED")
enum class MsfModuleType {
    EXPLOITS, POST, PAYLOADS, ENCODERS, NOPS, FALLBACK;

    override fun toString(): String {
        return this.name.lowercase(Locale.getDefault())
    }
}