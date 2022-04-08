package me.d3s34.sqlmap.restapi

class ApiRoutes(
    baseUrl: String
) {
    private val baseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

    fun routeVersion() = "${baseUrl}/version"
    fun routeNewTask() = "${baseUrl}/task/new"
    fun routeStartTask(taskId: String) = "${baseUrl}/scan/${taskId}/start"
    fun routeStopTask(taskId: String) = "${baseUrl}/scan/${taskId}/stop"
    fun routeStatusTask(taskId: String) = "${baseUrl}/scan/${taskId}/status"
    fun routeListTask(taskId: String) = "${baseUrl}/scan/${taskId}/list"
    fun routeDataTask(taskId: String) = "${baseUrl}/scan/${taskId}/data"
    fun routeLogTask(taskId: String) = "${baseUrl}/scan/${taskId}/log"
    fun routeKillTask(taskId: String) = "${baseUrl}/scan/${taskId}/kill"
    fun routeDeleteTask(taskId: String) = "${baseUrl}/scan/${taskId}/delete"
}