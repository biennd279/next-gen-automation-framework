package me.d3s34.metasploit

import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.d3s34.metasploit.rpcapi.ApiService
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.request.console.*
import kotlin.coroutines.CoroutineContext

class MetasploitRpcEngine(
    val url: String,
    val username: String,
    val password: String,
    override val coroutineContext: CoroutineContext
) : MetasploitEngine() {

    val apiService = ApiService(url)
    lateinit var token: String

    suspend fun init() {
        val loginResponse = apiService.login(LoginRequest(username, password))
        token = apiService.persistentToken(loginResponse.token)
    }

    suspend fun createConsole(): String {
        val response = apiService.createConsole(
            ConsoleCreateRequest(token)
        )

        return response.id
    }

    suspend fun writeConsole(id: String, input: String): Int {
        val response = apiService.writeConsole(
            ConsoleWriteRequest(token, id, input)
        )

        return response.wrote
    }

    suspend fun readConsole(id: String): Triple<String, String, Boolean> {
        val response = apiService.readConsole(
            ConsoleReadRequest(token, id)
        )

        return Triple(response.data, response.prompt, response.busy)
    }

    suspend fun deleteConsole(id: String): Boolean {
        val response = apiService.destroyConsole(
            ConsoleDestroyRequest(token, id)
        )

        return !response.error
    }

    suspend fun tabConsole(id: String, input: String): List<String> {
        val response = apiService.tabsConsole(
            ConsoleTabsRequest(token, id, input)
        )

        return response.tabs
    }

    fun startConsole(stdin: ReceiveChannel<String>, stdout: SendChannel<String>, timeRefresh: Long = 500): Job {
        return launch {
            val id = createConsole()
            val readLatest =  MutableStateFlow(false)

            launch {
                for (input in stdin) {
//                    while (!readLatest.value) {
//                        delay(timeRefresh)
//                    }

                    runCatching {
                        println("Send $input")
                        writeConsole(id, input)
                    }

                    readLatest.update { false }
                }
            }

            launch {

                do {
                    val (response, busy) = readConsole(id)
//                            stdout.send(response)
                    if (response.isNotBlank()) {
                        println("Receive: $response")
                    }
                    delay(timeRefresh)
                } while (true)
            }
        }
    }
}
