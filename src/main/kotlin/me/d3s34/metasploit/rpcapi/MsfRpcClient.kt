package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*

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

    fun login() {

    }

    companion object {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                messagePack()
            }

            install(DefaultRequest) {
                contentType(MessagePackContentType)
            }
        }
    }
}
