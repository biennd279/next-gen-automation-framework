package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
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
@kotlinx.serialization.Serializable
data class Data(
    val type: String,
    val data: List<Long>,
)

@kotlinx.serialization.Serializable
data class LoginSuccess(
    val result: Data,
    val token: Data
)

@OptIn(ExperimentalTime::class)
fun main() {

}