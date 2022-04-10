package me.d3s34.sqlmap

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import me.d3s34.sqlmap.restapi.ApiService
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.restapi.response.TaskDataResponse
import kotlin.coroutines.CoroutineContext

class SqlmapApiEngine(
    private val baseUrl: String,
    override val coroutineContext: CoroutineContext = Dispatchers.IO,
    private val refreshTime: Long = 5000
): SqlmapEngine() {
    private val apiService by lazy { ApiService(baseUrl) }

    suspend fun verifySqlInjection(request: StartTaskRequest): Boolean {
        val sqlmapProcess = SqlmapProcess(apiService)

        try {
            sqlmapProcess.start(request)

            while (this.isActive && sqlmapProcess.isRunning) { delay(refreshTime) }

            if (!isActive) { sqlmapProcess.stop() }

            val data = sqlmapProcess.getResponse().data

            return data != null && data.isNotEmpty()
        } catch (_: Throwable) {
            sqlmapProcess.stop()
            return false
        }
    }

    suspend fun attack(request: StartTaskRequest): TaskDataResponse? {
        val sqlmapProcess = SqlmapProcess(apiService)

        try {
            sqlmapProcess.start(request)

            while (this.isActive && sqlmapProcess.isRunning) { delay(refreshTime) }

            if (!isActive) { sqlmapProcess.stop() }

            return sqlmapProcess.getResponse()
        } catch (t: Throwable) {
            sqlmapProcess.stop()
            return null
        }
    }

    suspend fun update(): Boolean {
        val sqlmapProcess = SqlmapProcess(apiService)

        try {
            sqlmapProcess.start {
                updateAll = true
            }

            while (this.isActive && sqlmapProcess.isRunning) { delay(refreshTime) }

            if (!isActive) {
                sqlmapProcess.stop()
                return false
            }

            return sqlmapProcess.getResponse().success
        } catch (_: Throwable) {
            sqlmapProcess.stop()
            return false
        }
    }

    suspend fun purgeCache(): Boolean {
        val sqlmapProcess = SqlmapProcess(apiService)

        try {
            sqlmapProcess.start {
                cleanup = true
            }

            while (this.isActive && sqlmapProcess.isRunning) { delay(refreshTime) }

            if (!isActive) {
                sqlmapProcess.stop()
                return false
            }
            return sqlmapProcess.getResponse().success
        } catch (_: Throwable) {
            sqlmapProcess.stop()
            return false
        }
    }

}

suspend fun SqlmapApiEngine.attack(lambda: SqlmapRequestBuilder.() -> Unit): TaskDataResponse? {
    return attack(startTaskRequest { lambda() })
}
