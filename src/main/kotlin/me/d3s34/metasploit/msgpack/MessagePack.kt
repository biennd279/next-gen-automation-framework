package me.d3s34.metasploit.msgpack

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule

class MessagePack(
    override val serializersModule: SerializersModule = SerializersModule {
        contextual(Any::class, MessagePackSerializer)
    }): BinaryFormat {

    private val messagePacker = MessagePacker()

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val messagePackEncoder = MessagePackEncoder(serializersModule, messagePacker)
        messagePackEncoder.encodeSerializableValue(serializer, value)
        return messagePackEncoder.buffer.toByteArray()
    }
}