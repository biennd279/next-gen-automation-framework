package me.d3s34.metasploit.rpcapi.response

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import me.d3s34.metasploit.rpcapi.MsfRpcClient
import me.d3s34.metasploit.rpcapi.request.LoginRequest

@kotlinx.serialization.Serializable
sealed class MsfRpcResponse(
    val error: Boolean = false,
    @SerialName("error_class")
    val errorClass: String = "",
    @SerialName("error_message")
    val errorMessage: String = "",
    @SerialName("error_string")
    val errorString: String = "",
    @SerialName("error_backtrace")
    val errorBacktrace: List<String>? = listOf()
)

fun main() {
    val loginRequest = LoginRequest("usernamex", "password")

    val client = MsfRpcClient.client

    val response = runBlocking {
        client.post("http://localhost:55553/api/") {
            setBody(loginRequest)
        }.body() as LoginResponse
    }

    println(response)

    println(response.error)

    println(response.errorMessage)

    println(response.errorString)

    println(response.errorClass)
}