package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.d3s34.metasploit.rpcapi.request.LoginRequest

class MsfRpcClient(
    val host: String,
    val username: String,
    val password: String
) {
    private val token: String
        get() {
            if (_token == null) {


            }

            return _token!!
        }

    private var _token: String? = null

    private val apiUrl = if (host.endsWith("/")) host else host.dropLast(1) + "/"

    private val loginRequest = LoginRequest(username, password)

    suspend fun login() {
        client.post(apiUrl) {
            setBody(loginRequest)
        }

        //TODO
    }

    companion object {
        val client = HttpClient(CIO) {
            install(DefaultRequest) {
                contentType(MessagePackContentType)
            }

            install(MsfRpc)

            install(ContentNegotiation) {
                messagePack()
            }

            engine {
                proxy = ProxyBuilder.http("http://localhost:8080/")
            }
        }
    }
}
