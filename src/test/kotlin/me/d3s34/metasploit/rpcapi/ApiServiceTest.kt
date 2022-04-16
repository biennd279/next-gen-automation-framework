package me.d3s34.metasploit.rpcapi

import kotlinx.coroutines.runBlocking
import me.d3s34.metasploit.rpcapi.request.auth.TokenAddRequest
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.request.auth.LogoutRequest
import me.d3s34.metasploit.rpcapi.request.console.*
import me.d3s34.metasploit.rpcapi.request.core.ModuleStatsRequest
import me.d3s34.metasploit.rpcapi.request.core.ThreadListRequest
import me.d3s34.metasploit.rpcapi.request.job.JobListRequest
import me.d3s34.metasploit.rpcapi.response.core.toListThread
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ApiServiceTest {

    private val apiService = ApiService("http://localhost:55553/api/")

    private val token = runBlocking {
        apiService.login(
            LoginRequest(
                "username",
                "password"
            )
        ).token
    }

    @Test
    fun login(): Unit = runBlocking {
        val loginResponse = apiService.login(LoginRequest(
            "username",
            "password"
        ))

        assertNotNull(loginResponse)
        assertNotEquals(0, loginResponse.token.length)
    }

    @Test
    fun persistentToken() {
        val response = runBlocking {
            apiService.persistentToken(token)
        }

        assertNotEquals(0, response.length)
    }

    @Test
    fun addToken() {
        val response = runBlocking {
            apiService.addToken(TokenAddRequest(
                token,
                "abcd"
            ))
        }

        assertNotEquals("", response.result)
    }

    @Test
    fun logout() {

        val response = runBlocking {
            apiService.logout(LogoutRequest(
                token,
                token
            ))
        }

        assertNotEquals("", response.result)
    }

    @Test
    fun removeToken() {
    }

    @Test
    fun version() {
    }

    @Test
    fun addModule() {
    }

    @Test
    fun statsModule() {
        val response = runBlocking {
            apiService.statsModule(
                ModuleStatsRequest(
                    token
                )
            )
        }
        assertNotEquals(0, response.map.size)
    }

    @Test
    fun reloadModule() {
    }

    @Test
    fun saveCore() {
    }

    @Test
    fun stopCore() {
    }

    @Test
    fun setOptionsGlobal() {
    }

    @Test
    fun unsetOptionGlobal() {
    }

    @Test
    fun listThread() {
        val response = runBlocking {
            apiService.listThread(ThreadListRequest(token))
        }
        assertNotEquals(0, response.toListThread().size)
    }

    @Test
    fun killThread() {
    }

    @Test
    fun createConsole() {
        val response = runBlocking {
            apiService.createConsole(ConsoleCreateRequest(
                token
            ))
        }

        assertNotEquals(true, response.error)
    }

    @Test
    fun destroyConsole() {
    }

    @Test
    fun listConsole() {
        val response = runBlocking {
            apiService.listConsole(ConsoleListRequest(
                token
            ))
        }

        assertNotEquals(true, response.error)
        assertNotEquals(0, response.consoles)
    }

    @Test
    fun writeConsole() {
        val id = runBlocking {
            apiService.createConsole(ConsoleCreateRequest(
                token
            )).id
        }

        val response = runBlocking {
            apiService.writeConsole(ConsoleWriteRequest(
                token,
                id,
                "version\n"
            ))
        }

        assertNotEquals(true, response.error)
        assertNotEquals(0, response.wrote)
    }

    @Test
    fun readConsole() {
        val id = runBlocking {
            apiService.createConsole(ConsoleCreateRequest(
                token
            )).id
        }

        runBlocking {
            apiService.writeConsole(ConsoleWriteRequest(
                token,
                id,
                "version\n"
            ))
        }

        val response = runBlocking {
            apiService.readConsole(ConsoleReadRequest(
                token,
                id
            ))
        }

        assertNotEquals(true, response.error)
        assertNotEquals(0, response.data.length)
        assertNotEquals(0, response.prompt.length)
    }

    @Test
    fun detachSession() {
    }

    @Test
    fun killSession() {
    }

    @Test
    fun tabsConsole() {
        val id = runBlocking {
            apiService.createConsole(ConsoleCreateRequest(
                token
            )).id
        }

        val response = runBlocking {
            apiService.tabsConsole(ConsoleTabsRequest(
                token,
                id,
                "he"
            ))
        }

        assertNotEquals(true, response.error)
        assertNotEquals(0, response.tabs.size)
    }

    @Test
    fun listJob() {
        val response = runBlocking {
            apiService.listJob(
                JobListRequest(token)
            )
        }

        assertNotEquals(true, response.error)
        println(response)
    }

    @Test
    fun getInfoJob() {
    }

    @Test
    fun stopJob() {
    }
}