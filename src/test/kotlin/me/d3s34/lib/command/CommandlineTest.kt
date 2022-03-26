package me.d3s34.lib.command

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

internal class CommandlineTest {

    private lateinit var commandline: Commandline

    @BeforeTest
    fun init() {
        commandline = buildCommand {
            path = "echo"
            args = listOf("hello", "world")
            shortFlag = mapOf("n" to null)
        }
    }

    @Test
    fun getEscapedArgs() {
        assertEquals(listOf( "-n", "hello", "world"), commandline.escapedArgs)
    }

    @Test
    fun toCommandline() {
        assertEquals("echo -n hello world", commandline.toCommandline())
    }

    @Test
    fun testToString() {
        assertEquals("echo -n hello world", commandline.toString())

    }

    @Test
    fun getPath() {
        assertEquals("echo", commandline.path)
    }
}