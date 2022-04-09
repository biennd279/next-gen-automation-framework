package me.d3s34.sqlmap

import kotlinx.coroutines.runBlocking
import me.d3s34.sqlmap.restapi.ApiService
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.restapi.response.TaskDataResponse

class SqlmapProcess(
    private val apiService: ApiService
) {
    val taskId: String = runBlocking { apiService.createNewTask().taskId!! }

    val isRunning: Boolean
        get() = runBlocking { apiService.getTaskStatus(taskId).status == "running" }

    suspend fun start(request: StartTaskRequest): Boolean {
        return apiService.startTask(taskId, request)
            .success
    }

    suspend fun getResponse(): TaskDataResponse {
        return apiService.getTaskData(taskId)
    }

    suspend fun stop(): Boolean {
        return !isRunning || apiService.stopTask(taskId).success
    }
}

suspend fun SqlmapProcess.start(lambda: SqlmapRequestBuilder.() -> Unit): Boolean {
    return this.start(startTaskRequest { lambda() })
}