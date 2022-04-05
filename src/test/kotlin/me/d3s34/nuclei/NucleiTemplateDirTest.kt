package me.d3s34.nuclei

import me.d3s34.nuclei.NucleiTemplateDir
import me.d3s34.nuclei.NucleiTemplateFile
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import java.io.File
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal class NucleiTemplateDirTest {

    private val rootDir = "/tmp/nuclei/"
    private val listFolder = listOf("cves", "test", "cves/2021")
    private val listFile = listOf("test.yaml", "cves/2021/CVE-2021-11234.yml", "test/test.yaml")

    @BeforeEach
    fun setUp() {
        listFolder.forEach {
            val file =  File(rootDir + it)
            if (!file.exists()) {
                file.mkdirs()
            }
        }

        listFile.forEach {
            val file = File(rootDir + it)
            if (!file.exists()) {
                file.createNewFile()
            }
        }
    }

    @AfterEach
    fun tearDown() {
        listFolder.forEach { File(rootDir + it).deleteOnExit() }

        listFile.forEach { File(rootDir + it).deleteOnExit() }
    }

    @Test
    fun getTemplates() {
        val rootTemplate = NucleiTemplateDir(rootDir)
        val templates = rootTemplate.getTemplates()

        assertEquals(3, templates.size)

        listFolder.filter { it.contains("/").not() }
            .forEach {
                assertContains(templates, NucleiTemplateDir(rootDir + it) )
            }

        listFile.filter { it.contains("/").not() }
            .forEach {
                assertContains(templates, NucleiTemplateFile(rootDir + it) )
            }
    }
}