package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest
import me.d3s34.metasploit.rpcapi.request.auth.TokenAddRequest
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.request.auth.LogoutRequest
import me.d3s34.metasploit.rpcapi.request.auth.TokenRemoveRequest
import me.d3s34.metasploit.rpcapi.request.console.*
import me.d3s34.metasploit.rpcapi.request.core.*
import me.d3s34.metasploit.rpcapi.response.InfoResponse
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse
import me.d3s34.metasploit.rpcapi.response.auth.LoginResponse
import me.d3s34.metasploit.rpcapi.response.console.*
import me.d3s34.metasploit.rpcapi.response.core.CoreModuleResponse
import me.d3s34.metasploit.rpcapi.response.core.ThreadListResponse
import me.d3s34.metasploit.rpcapi.response.core.VersionResponse

class ApiService(
    val apiUrl: String
) {
    private suspend inline fun <reified T: MsfRpcRequest, reified U: MsfRpcResponse> sendRpc(request: T): U {
        return client.post(apiUrl) {
            setBody(request)
        }.handleMsfResponse()
    }

    suspend fun login(loginRequest: LoginRequest): LoginResponse = sendRpc(loginRequest)

    suspend fun persistentToken(tempToken: String, persistentToken: String? = null): String {

        val token = persistentToken ?: randomString(32, tokenCharSet)

        val tokenAddRequest = TokenAddRequest(
            tempToken,
            token
        )

        client.post(apiUrl) {
            setBody(tokenAddRequest)
        }.handleMsfResponse<InfoResponse>()

        return token
    }

    suspend fun addToken(tokenAddRequest: TokenAddRequest): InfoResponse = sendRpc(tokenAddRequest)

    suspend fun logout(logoutRequest: LogoutRequest): InfoResponse = sendRpc(logoutRequest)

    suspend fun removeToken(tokenRemoveRequest: TokenRemoveRequest): InfoResponse = sendRpc(tokenRemoveRequest)

    suspend fun version(versionRequest: VersionRequest): VersionResponse = sendRpc(versionRequest)

    suspend fun addModule(addModulePathRequest: AddModulePathRequest): CoreModuleResponse = sendRpc(addModulePathRequest)

    suspend fun statsModule(moduleStatsRequest: ModuleStatsRequest): CoreModuleResponse = sendRpc(moduleStatsRequest)

    suspend fun reloadModule(reloadModulesRequest: ReloadModulesRequest): CoreModuleResponse = sendRpc(reloadModulesRequest)

    suspend fun saveCore(coreSaveRequest: CoreSaveRequest): InfoResponse = sendRpc(coreSaveRequest)

    suspend fun stopCore(coreStopRequest: CoreStopRequest): InfoResponse = sendRpc(coreStopRequest)

    suspend fun setOptionsGlobal(setOptionGlobalRequest: SetOptionGlobalRequest): InfoResponse = sendRpc(setOptionGlobalRequest)

    suspend fun unsetOptionGlobal(unsetOptionGlobalRequest: UnsetOptionGlobalRequest): InfoResponse = sendRpc(unsetOptionGlobalRequest)

    suspend fun listThread(listThreadListRequest: ThreadListRequest): ThreadListResponse = sendRpc(listThreadListRequest)

    suspend fun killThread(killRequest: ThreadKillRequest): InfoResponse = sendRpc(killRequest)

    suspend fun createConsole(createConsoleRequest: CreateConsoleRequest): CreateConsoleResponse = sendRpc(createConsoleRequest)

    suspend fun destroyConsole(destroyConsoleRequest: DestroyConsoleRequest): InfoResponse = sendRpc(destroyConsoleRequest)

    suspend fun listConsole(listConsoleRequest: ListConsoleRequest): ConsoleListResponse = sendRpc(listConsoleRequest)

    suspend fun writeConsole(writeConsoleRequest: WriteConsoleRequest): ConsoleWriteResponse = sendRpc(writeConsoleRequest)

    suspend fun readConsole(readConsoleRequest: ReadConsoleRequest): ConsoleReadResponse = sendRpc(readConsoleRequest)

    suspend fun detachSession(sessionDetachRequest: SessionDetachRequest): InfoResponse = sendRpc(sessionDetachRequest)

    suspend fun killSession(sessionKillRequest: SessionKillRequest): InfoResponse = sendRpc(sessionKillRequest)

    suspend fun tabsConsole(tabsRequest: ConsoleTabsRequest): ConsoleTabsResponse = sendRpc(tabsRequest)



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
