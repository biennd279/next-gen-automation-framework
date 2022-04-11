package me.d3s34.sqlmap.restapi

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.restapi.response.*

class ApiService(
    baseUrl: String
) {
    private val apiRoutes = ApiRoutes(baseUrl)

    suspend fun getVersion(): VersionResponse {
        return client.get(apiRoutes.routeVersion()).body()
    }

    suspend fun createNewTask(): NewTaskResponse {
        return client.get(apiRoutes.routeNewTask()).body()
    }

    suspend fun startTask(id: String, startTaskRequest: StartTaskRequest): StartTaskResponse {
        return client.post(apiRoutes.routeStartTask(id)) {
            setBody(startTaskRequest)
        }.body()
    }

    suspend fun stopTask(id: String): StopTaskResponse {
        return client.get(apiRoutes.routeStopTask(id)).body()
    }

    suspend fun getTaskData(id: String): TaskDataResponse {
        return client.get(apiRoutes.routeDataTask(id)).body()
    }

    suspend fun getTaskLog(id: String): TaskLogResponse {
        return client.get(apiRoutes.routeLogTask(id)).body()
    }

    suspend fun killTask(id: String): KillTaskResponse {
        return client.get(apiRoutes.routeKillTask(id)).body()
    }

    suspend fun deleteTask(id: String): DeleteTaskResponse {
        return client.get(apiRoutes.routeDeleteTask(id)).body()
    }

    suspend fun getTaskStatus(id: String): StatusTaskResponse {
        return client.get(apiRoutes.routeStatusTask(id)).body()
    }

    suspend fun getTaskDataAsString(id: String): String {
        return client.get(apiRoutes.routeDataTask(id)).body()
    }

    companion object {

        private val jsonFormat = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        private val client = HttpClient(CIO) {

            install(ContentNegotiation) {
                json(jsonFormat)
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}