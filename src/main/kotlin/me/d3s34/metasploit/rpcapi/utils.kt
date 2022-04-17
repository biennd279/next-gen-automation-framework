package me.d3s34.metasploit.rpcapi

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import me.d3s34.lib.msgpack.MessagePack
import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse
import java.nio.charset.Charset

@OptIn(InternalSerializationApi::class)
fun MsfRpcRequest.toMsfRequest(): List<Any> {
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

fun randomString(length: Int, charSet: List<Char>): String {
    return buildString {
        repeat(length) {
            val randomPost = kotlin.random.Random.nextInt(0, charSet.size)
            append(charSet[randomPost])
        }
    }
}
val tokenCharSet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

suspend inline fun <reified T: MsfRpcResponse> HttpResponse.handleMsfResponse(): T {
    when (status) {
        HttpStatusCode.BadGateway -> throw Throwable("404")
        HttpStatusCode.Unauthorized -> throw Throwable("401")
        HttpStatusCode.Forbidden -> throw Throwable("403")
        HttpStatusCode.NotFound -> throw Throwable("404")
    }

    return runCatching<T> {
        val response = body<T>()
        if (response.error) {
            throw Throwable("Request error ${response.errorCode}")
        }

        return response
    }
        .getOrThrow()
}


object EmptyResponse: MsfRpcResponse()

@Suppress("UNCHECKED_CAST")
fun <T: MsfRpcResponse> emptyResponse() = EmptyResponse as T

fun Any?.toByteArrayString(): String {
    if (this is ByteArray)
        return this.toString(Charset.defaultCharset())
    return this.toString()
}
