package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import me.d3s34.metasploit.msgpack.messagePack

class MsRpcClient {

    companion object {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                messagePack()
            }

            install(DefaultRequest) {
                contentType(MPContentType.MessagePack)
            }

            engine {
                proxy = ProxyBuilder.http("http://127.0.0.1:8080/")
            }

        }
    }
}


fun main() {
    val client = MsRpcClient.client

    val loginRequest = listOf("auth.login", "username", "password")

    val response = runBlocking {
        client.post("http://localhost:55553/api/") {
            setBody(loginRequest)
            contentType(MPContentType.MessagePack)
        }.body() as Map<String, Any>
    }

    println(response)
}