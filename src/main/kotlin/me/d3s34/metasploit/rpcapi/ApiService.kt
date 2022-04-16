package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import me.d3s34.metasploit.rpcapi.request.auth.AddTokenRequest
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse
import me.d3s34.metasploit.rpcapi.response.auth.LoginResponse

class ApiService(
    private val host: String,
    private val username: String,
    private val password: String
) {
    private val apiUrl = if (host.endsWith("/")) host else host.dropLast(1) + "/"

    suspend fun login(): LoginResponse {
        val loginRequest = LoginRequest(username, password)

        val response = client.post(apiUrl) {
            setBody(loginRequest)
        }

        return response.handleMsfResponse()
    }

    suspend fun persistentToken(tempToken: String): MsfRpcResponse {
        val randomToken = randomString(32, tokenCharSet)
        val addTokenRequest = AddTokenRequest(
            tempToken,
            randomToken
        )

        val response = client.post(apiUrl) {
            setBody(addTokenRequest)
        }

        return response.handleMsfResponse()
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
