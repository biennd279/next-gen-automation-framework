package me.d3s34.metasploit.rpcapi.request

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
fun AbstractRequest.toMsfRequest(): List<Any> {
    val requestEncoder = RequestEncoder()
    @Suppress("UNCHECKED_CAST")
    requestEncoder.encodeSerializableValue(this::class.serializer() as KSerializer<Any>, this)
    val payload = requestEncoder.getListByRightOrder().filterNotNull()

    return buildList {
        add("${group}.${method}")
        addAll(payload)
    }
}
