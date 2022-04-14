package me.d3s34.metasploit.rpcapi.request

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.properties.encodeToMap

@kotlinx.serialization.Serializable
sealed class AbstractRequest {
    abstract val group: String
    abstract val method: String
}

internal val serializersModule = SerializersModule {
    polymorphic(AbstractRequest::class) {
        subclass(LoginRequest::class, LoginRequest.serializer())
    }
}
