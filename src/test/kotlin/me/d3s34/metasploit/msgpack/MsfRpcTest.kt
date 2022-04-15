package me.d3s34.metasploit.msgpack

import kotlinx.serialization.SerialName
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import me.d3s34.lib.msgpack.MessagePack
import me.d3s34.lib.msgpack.decodeHex
import org.junit.jupiter.api.Test
import java.nio.charset.Charset
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals


internal class MsfRpcTest {

    @kotlinx.serialization.Serializable
    data class TestClass (
        val name: String
    )

    @kotlinx.serialization.Serializable
    data class LoginSuccess(
        val result: String,
        val token: String
    )
    @kotlinx.serialization.Serializable
    @JvmInline
    value class InlineClass(val value: Int)

    private val bien = TestClass("bien")
    private val byteArray = byteArrayOf(12, 14)
    private val inlineClass = InlineClass(1)

    private val test = buildMap {
        put(1, "01")
        put(null, "c0")
        put(10.2, "cb4024666666666666")
        put("abc", "a3616263")
        put(listOf(1, 2, 3), "93010203")
        put(mapOf("abd" to "a"), "81a3616264a161")
        put(byteArray, "920c0e")
        put(bien, "81a46e616d65a46269656e")
        put(inlineClass, "01")
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

        assertContentEquals(
            byteArray,
            messagePack.decodeFromByteArray(ByteArraySerializer(), test[byteArray]!!.decodeHex())
        )

        val response = ("82c406726573756c74c40773756363657373c405746f6b656ec42054454d507636674b6e53453" +
                "14d5a4c656c47563659645862726c3332546b6349")
            .decodeHex()

        val loginSuccess = messagePack.decodeFromByteArray<LoginSuccess>(response)

        assertEquals(LoginSuccess("success", "TEMPv6gKnSE1MZLelGV6YdXbrl32TkcI"), loginSuccess)

        assertEquals(inlineClass, messagePack.decodeFromByteArray(test[inlineClass]!!.decodeHex()))
    }

    @Test
    fun encodeToByteArray() {
        val messagePack = MessagePack()
        val encoded = test.keys.map { messagePack.encodeToByteArray(it).decodeHex() }
        assertContentEquals(test.values, encoded)
    }
}