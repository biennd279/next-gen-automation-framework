package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import me.d3s34.metasploit.msgpack.MessagePack
import me.d3s34.metasploit.msgpack.decodeHex
import kotlin.time.ExperimentalTime

class MsRpcClient {

    companion object {
        val client = HttpClient(CIO) {
//            install(MessagePack)

//            defaultRequest {
//                accept(MessPackContentType.MessagePack)
//                contentType(MessPackContentType.MessagePack)
//            }

            engine {
                proxy = ProxyBuilder.http("http://127.0.0.1:8080/")
            }
        }
    }
}
