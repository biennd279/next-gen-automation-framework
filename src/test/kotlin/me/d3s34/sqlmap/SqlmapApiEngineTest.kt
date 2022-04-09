package me.d3s34.sqlmap

import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

internal class SqlmapApiEngineTest {

    private val apiEngine = SqlmapApiEngine("http://127.0.0.1:8775/")

    @Test
    fun verifySqlInjection(): Unit = runBlocking {
        val test1= launch {
            val response = apiEngine.verifySqlInjection(startTaskRequest {
                url = "http://localhost/"
                data = "user=test&password=test&s=OK"
                freshQueries = true
            })

            assertEquals(true, response)
        }

        val test2 = launch {
            val response = apiEngine.verifySqlInjection(startTaskRequest {
                url = "http://localhost/?test=1"
                freshQueries = true
            })

            assertEquals(false, response)
        }

        joinAll(test1, test2)
    }

    @Test
    fun attack(): Unit = runBlocking {
        val response = apiEngine.attack {
            url = "http://localhost/"
            data = "user=test&password=test&s=OK"
            dumpAll = true
            getCount = true
            getRoles = true
            getDbs = true
        }

        assertNotNull(response)
        assertEquals(true, response.success)
        assertNotNull(response.data)
        assertNotEquals(0, response.data!!.size)
    }

    @Test
    fun attachCancel(): Unit = runBlocking {
        val job = launch {
            val response = apiEngine.attack {
                url = "http://localhost/"
                data = "user=test&password=test&s=OK"
                dumpAll = true
                getCount = true
                getRoles = true
                getDbs = true
            }
        }

        delay(3000)
        job.cancel()
    }
}