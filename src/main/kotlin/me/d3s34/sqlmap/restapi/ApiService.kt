package me.d3s34.sqlmap.restapi

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.restapi.response.*

class ApiService(
    baseUrl: String
) {
    private val apiRoutes = ApiRoutes(baseUrl)

    suspend fun getVersion(): VersionResponse {
        return client.get(apiRoutes.routeVersion())
    }

    suspend fun createNewTask(): NewTaskResponse {
        return client.get(apiRoutes.routeNewTask())
    }

    suspend fun startTask(id: String, startTaskRequest: StartTaskRequest): StartTaskResponse {
        return client.post(apiRoutes.routeStartTask(id)) {
            body = startTaskRequest
        }
    }

    suspend fun stopTask(id: String): StopTaskResponse {
        return client.get(apiRoutes.routeStopTask(id))
    }

    suspend fun getTaskData(id: String): TaskDataResponse {
        return client.get(apiRoutes.routeDataTask(id))
    }

    suspend fun getTaskLog(id: String): TaskLogResponse {
        return client.get(apiRoutes.routeLogTask(id))
    }

    suspend fun killTask(id: String): KillTaskResponse {
        return client.get(apiRoutes.routeKillTask(id))
    }

    suspend fun deleteTask(id: String): DeleteTaskResponse {
        return client.get(apiRoutes.routeDeleteTask(id))
    }

    suspend fun getTaskStatus(id: String): StatusTaskResponse {
        return client.get(apiRoutes.routeStatusTask(id))
    }

    companion object {

        private val jsonFormat = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }

        private val client = HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(jsonFormat)
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }
}