package me.d3s34.metasploit.msgpack

import kotlinx.serialization.encodeToByteArray
import org.junit.jupiter.api.Test

internal class MessagePackTest {

    @Test
    fun decodeFromByteArray() {
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun encodeToByteArray() {
        val messagePack = MessagePack()

        val loginRequest = mapOf(1 to 12, 3 to 111423441224)

        val decoded = messagePack.encodeToByteArray(loginRequest)
            .toUByteArray()
            .toList()

        println(decoded)
    }
}