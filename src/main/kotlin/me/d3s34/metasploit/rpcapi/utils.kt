package me.d3s34.metasploit.rpcapi

import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.d3s34.lib.msgpack.MessagePack
import me.d3s34.metasploit.rpcapi.request.AbstractRequest
import me.d3s34.metasploit.rpcapi.request.RequestEncoder

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

fun Configuration.messagePack() {
    serialization(MessagePackContentType, MessagePack())
}