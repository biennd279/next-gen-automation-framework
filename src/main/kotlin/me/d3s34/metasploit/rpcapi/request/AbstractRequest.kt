package me.d3s34.metasploit.rpcapi.request

import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@kotlinx.serialization.Serializable
sealed class AbstractRequest {
    abstract val group: String
    abstract val method: String
}
