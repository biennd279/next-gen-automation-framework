package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*

class MsRpcClient {

    companion object {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                messagePack()
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
