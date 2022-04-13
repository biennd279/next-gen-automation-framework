package me.d3s34.metasploit.msgpack

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


internal class MessagePackTest {

    @kotlinx.serialization.Serializable
    data class TestClass (
        val name: String
    )

    private val bien = TestClass("bien")

    private val test = buildMap {
        put(1, "01")
        put(null, "c0")
        put(10.2, "cb4024666666666666")
        put("abc", "a3616263")
        put(listOf(1, 2, 3), "93010203")
        put(mapOf("abd" to "a"), "81a3616264a161")
        put(byteArrayOf(12, 14), "920c0e")
        put(bien, "81a46e616d65a46269656e")
    }

    @Test
    fun decodeFromByteArray() {
        val messagePack = MessagePack()
        assertEquals(1, messagePack.decodeFromByteArray(test[1]!!.decodeHex()))
        assertEquals(10.2, messagePack.decodeFromByteArray(test[10.2]!!.decodeHex()))
        assertEquals("abc", messagePack.decodeFromByteArray(test["abc"]!!.decodeHex()))
        assertEquals(mapOf("abd" to "a"),
            messagePack.decodeFromByteArray(test[mapOf("abd" to "a")]!!.decodeHex()))
        assertContentEquals(listOf(1, 2, 3),
            messagePack.decodeFromByteArray<List<Int>>(test[listOf(1, 2, 3)]!!.decodeHex()))
        assertEquals(bien, messagePack.decodeFromByteArray(test[bien]!!.decodeHex()))


//        assertEquals(byteArrayOf(12, 14).toString(Charset.defaultCharset()),
//            messagePack.decodeFromByteArray<String>(test[byteArrayOf(12, 14)]!!.decodeHex()))
    }

    @Test
    fun encodeToByteArray() {
        val messagePack = MessagePack()

        val encoded = test.keys.map { messagePack.encodeToByteArray(it).decodeHex() }
        assertContentEquals(test.values, encoded)
    }
}