package org.zaproxy.addon.naf.component.tab

import androidx.compose.runtime.mutableStateOf
import me.d3s34.sqlmap.SqlmapApiEngine
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.restapi.response.TaskDataResponse

class SqlmapTabComponent(
    val sqlmapEngine: SqlmapApiEngine
): ExploitTabComponent(title = "Sqlmap") {
    val startRequestState = mutableStateOf(StartTaskRequest())
    val responseState = mutableStateOf<TaskDataResponse?>(null)
    val status = mutableStateOf(Status.NOT_RUN)

    override suspend fun exploit() {
        responseState.value = kotlin.runCatching {
            status.value = Status.RUNNING
            sqlmapEngine.attack(startRequestState.value)
        }
            .onFailure {
                status.value = Status.ERROR
            }
            .onSuccess {
                status.value = Status.DONE
            }
            .getOrDefault(responseState.value)
    }

    override suspend fun onClose() {}

    enum class Status {
        NOT_RUN, ERROR, RUNNING, DONE
    }
}