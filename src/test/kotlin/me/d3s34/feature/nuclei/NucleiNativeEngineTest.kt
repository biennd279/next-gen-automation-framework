package me.d3s34.feature.nuclei

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

internal class NucleiNativeEngineTest {

    @Test
    fun scan() {
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            println("Handler catch")
        }
        val home = System.getProperty("user.home")
        val nucleiEngine = NucleiNativeEngine(
            "${home}/go/bin/nuclei",
            Dispatchers.Default + exceptionHandler
        )

        val result = nucleiEngine.scan(
            "d3s34.me",
            NucleiTemplateDir("${home}/nuclei-templates/dns/")
        ) {
            delay(100)
            it.cancel()
        }

        assertNotEquals(0, result.size)
        assertEquals(4, result.size)
    }

    @Test
    fun updateTemplate() {

        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            println("Handler catch")
        }

        val home = System.getProperty("user.home")
        val nucleiEngine = NucleiNativeEngine(
            "${home}/go/bin/nuclei",
            Dispatchers.Default + exceptionHandler
        )

        val tempDir = File("/tmp/nucleiTemp").apply {
            if (exists()) {
                delete()
            }
            mkdirs()
        }


        nucleiEngine.updateTemplate(
            NucleiTemplateDir(tempDir.path)
        ) {
//            delay(100)
            it.cancel()
        }

        val templates = tempDir.walk()
            .maxDepth(1)
            .toList()

        assertNotEquals(1, templates.size)

        tempDir.apply {
            if (exists()) {
                delete()
            }
        }

    }
}