package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest
import me.d3s34.metasploit.rpcapi.request.auth.LoginRequest
import me.d3s34.metasploit.rpcapi.request.auth.LogoutRequest
import me.d3s34.metasploit.rpcapi.request.auth.TokenAddRequest
import me.d3s34.metasploit.rpcapi.request.auth.TokenRemoveRequest
import me.d3s34.metasploit.rpcapi.request.console.*
import me.d3s34.metasploit.rpcapi.request.core.*
import me.d3s34.metasploit.rpcapi.request.job.JobInfoRequest
import me.d3s34.metasploit.rpcapi.request.job.JobListRequest
import me.d3s34.metasploit.rpcapi.request.job.JobStopRequest
import me.d3s34.metasploit.rpcapi.request.module.ModuleListRequest
import me.d3s34.metasploit.rpcapi.request.module.ModuleOptionsRequest
import me.d3s34.metasploit.rpcapi.response.InfoResponse
import me.d3s34.metasploit.rpcapi.response.MsfRpcResponse
import me.d3s34.metasploit.rpcapi.response.auth.LoginResponse
import me.d3s34.metasploit.rpcapi.response.console.*
import me.d3s34.metasploit.rpcapi.response.core.CoreModuleResponse
import me.d3s34.metasploit.rpcapi.response.core.ThreadListResponse
import me.d3s34.metasploit.rpcapi.response.core.VersionResponse
import me.d3s34.metasploit.rpcapi.response.job.JobInfoResponse
import me.d3s34.metasploit.rpcapi.response.job.JobListResponse
import me.d3s34.metasploit.rpcapi.response.module.ModuleListResponse
import me.d3s34.metasploit.rpcapi.response.module.ModuleOptionsResponse

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

    suspend fun addModule(modulePathAddRequest: ModulePathAddRequest): CoreModuleResponse = sendRpc(modulePathAddRequest)

    suspend fun statsModule(moduleStatsRequest: ModuleStatsRequest): CoreModuleResponse = sendRpc(moduleStatsRequest)

    suspend fun reloadModule(modulesReloadRequest: ModulesReloadRequest): CoreModuleResponse = sendRpc(modulesReloadRequest)

    suspend fun saveCore(coreSaveRequest: CoreSaveRequest): InfoResponse = sendRpc(coreSaveRequest)

    suspend fun stopCore(coreStopRequest: CoreStopRequest): InfoResponse = sendRpc(coreStopRequest)

    suspend fun setOptionsGlobal(optionGlobalSetRequest: OptionGlobalSetRequest): InfoResponse = sendRpc(optionGlobalSetRequest)

    suspend fun unsetOptionGlobal(optionGlobalUnsetRequest: OptionGlobalUnsetRequest): InfoResponse = sendRpc(optionGlobalUnsetRequest)

    suspend fun listThread(listThreadListRequest: ThreadListRequest): ThreadListResponse = sendRpc(listThreadListRequest)

    suspend fun killThread(killRequest: ThreadKillRequest): InfoResponse = sendRpc(killRequest)

    suspend fun createConsole(consoleCreateRequest: ConsoleCreateRequest): ConsoleCreateResponse = sendRpc(consoleCreateRequest)

    suspend fun destroyConsole(consoleDestroyRequest: ConsoleDestroyRequest): InfoResponse = sendRpc(consoleDestroyRequest)

    suspend fun listConsole(consoleListRequest: ConsoleListRequest): ConsoleListResponse = sendRpc(consoleListRequest)

    suspend fun writeConsole(consoleWriteRequest: ConsoleWriteRequest): ConsoleWriteResponse = sendRpc(consoleWriteRequest)

    suspend fun readConsole(consoleReadRequest: ConsoleReadRequest): ConsoleReadResponse = sendRpc(consoleReadRequest)

    suspend fun detachSession(sessionDetachRequest: SessionDetachRequest): InfoResponse = sendRpc(sessionDetachRequest)

    suspend fun killSession(sessionKillRequest: SessionKillRequest): InfoResponse = sendRpc(sessionKillRequest)

    suspend fun tabsConsole(tabsRequest: ConsoleTabsRequest): ConsoleTabsResponse = sendRpc(tabsRequest)

    suspend fun listJob(jobListRequest: JobListRequest): JobListResponse = sendRpc(jobListRequest)

    suspend fun getInfoJob(jobInfoRequest: JobInfoRequest): JobInfoResponse = sendRpc(jobInfoRequest)

    suspend fun stopJob(jobStopRequest: JobStopRequest): InfoResponse = sendRpc(jobStopRequest)

    suspend fun getModulesList(moduleListRequest: ModuleListRequest): ModuleListResponse = sendRpc(moduleListRequest)

    suspend fun getModuleOptions(moduleOptionsRequest: ModuleOptionsRequest): ModuleOptionsResponse = sendRpc(moduleOptionsRequest)

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
