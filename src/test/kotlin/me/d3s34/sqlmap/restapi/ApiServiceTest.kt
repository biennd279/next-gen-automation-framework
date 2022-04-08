package me.d3s34.sqlmap.restapi

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

internal class ApiServiceTest {

    private val apiService = ApiService("http://127.0.0.1:8775/")

    @Test
    fun getVersion(): Unit = runBlocking {
        val version = apiService.getVersion()
        assertEquals(true, version.success)
        assertNotNull(version, version.version)
    }

    @Test
    fun createNewTask(): Unit = runBlocking {
        val task = apiService.createNewTask()
        assertNotNull(task.taskId)
    }

    @Test
    fun startTask(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK"
        )
        val startTaskResponse = apiService.startTask(taskId, request)
        assertEquals(true, startTaskResponse.success)
    }

    @Test
    fun stopTask(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK",
            dumpAll = true
        )

        apiService.startTask(taskId, request)

        val stopResponse = apiService.stopTask(taskId)
        assertEquals(true, stopResponse.success)
    }

    @Test
    fun getTaskData(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK",
        )

        apiService.startTask(taskId, request)

        while (apiService.getTaskStatus(taskId).status == "running") { delay(1000) }

        val dataResponse = apiService.getTaskData(taskId)

        assertNotNull(dataResponse.data)
        assertNotEquals(0, dataResponse.data!!.size)
    }

    @Test
    fun getTaskLog(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK"
        )
        apiService.startTask(taskId, request)

        val logResponse = apiService.getTaskLog(taskId)

        assertNotNull(logResponse.log)
        assertEquals(0, logResponse.log!!.size)
    }

    @Test
    fun killTask(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK",
        )

        apiService.startTask(taskId, request)

        val killTask = apiService.killTask(taskId)
        assertEquals(true, killTask.success)
    }

    @Test
    fun deleteTask(): Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK",
        )

        apiService.startTask(taskId, request)
        val delTaskResponse = apiService.deleteTask(taskId)
        assertEquals(true, delTaskResponse.success)
    }

    @Test
    fun getTaskStatus():Unit = runBlocking {
        val taskId = apiService.createNewTask().taskId!!
        val request = StartTaskRequest(
            url = "http://localhost/",
            data = "user=test&password=test&s=OK"
        )
        apiService.startTask(taskId, request)

        val status = apiService.getTaskStatus(taskId)

        assertNotNull(status.status)
    }
}