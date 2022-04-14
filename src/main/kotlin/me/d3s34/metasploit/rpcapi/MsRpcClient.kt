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

class MsRpcClient {

    companion object {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                messagePack {
                }
            }

            install(DefaultRequest) {
                contentType(MessagePackContentType)
            }

            engine {
                proxy = ProxyBuilder.http("http://127.0.0.1:8080/")
            }

        }
    }
}

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: Boolean
)

fun main() {
    val client = MsRpcClient.client

    val loginRequest = listOf("auth.login", "username", "password")

    val response = runBlocking {
        client.post("http://localhost:55553/api/") {
            setBody(loginRequest)
            contentType(MessagePackContentType)
        }.body() as ErrorResponse
    }

    println(response)
}
