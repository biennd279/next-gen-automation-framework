package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.xml.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

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

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: Boolean? = false,
    @SerialName("error_class")
    val errorClass: String = "",
    @SerialName("error_message")
    val errorMessage: String = "",
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_backtrace")
    val errorBacktrace: List<String>
)

@OptIn(ExperimentalTime::class)
fun main() {
    val time = measureTime {
        val client = MsRpcClient.client

        val loginRequest = listOf("auth.login", "usernamex", "password")

        val response: ErrorResponse = runBlocking {
            client.post("http://localhost:55553/api/") {
                setBody(loginRequest)
            }.body()
        }

        println(response)
    }

    println(time)
}
