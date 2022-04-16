package me.d3s34.metasploit.rpcapi

import kotlinx.coroutines.runBlocking
import me.d3s34.metasploit.rpcapi.request.auth.TokenAddRequest
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.request.auth.LogoutRequest
import me.d3s34.metasploit.rpcapi.request.core.ModuleStatsRequest
import me.d3s34.metasploit.rpcapi.request.core.ThreadListRequest
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
}