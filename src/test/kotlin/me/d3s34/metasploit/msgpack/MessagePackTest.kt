package me.d3s34.metasploit.msgpack

import kotlinx.serialization.encodeToByteArray
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals



internal class MessagePackTest {

    @kotlinx.serialization.Serializable
    data class TestClass (
        val name: String
    )

    private val test = buildMap {
        put(1, "01")
        put(null, "c0")
        put(10.2, "cb4024666666666666")
        put("abc", "a3616263")
        put(listOf(1, 2, 3), "93010203")
        put(mapOf("abd" to "a"), "81a3616264a161")
        put(byteArrayOf(12, 14), "920c0e")
        put(TestClass("bien"), "81a46e616d65a46269656e")
    }


    @Test
    fun decodeFromByteArray() {
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun encodeToByteArray() {
        val messagePack = MessagePack()

        val encoded = test.keys.map { messagePack.encodeToByteArray(it).toHex() }
        assertContentEquals(test.values, encoded)
    }
}