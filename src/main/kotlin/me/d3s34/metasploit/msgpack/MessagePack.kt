package me.d3s34.metasploit.msgpack

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule

class MessagePack(
    override val serializersModule: SerializersModule = SerializersModule {
        contextual(Any::class, MessagePackSerializer)
    }
) : BinaryFormat {

    private val messagePacker = MessagePacker()

    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val decoder = MessagePackDecoder(serializersModule, bytes)
        return decoder.decodeSerializableValue(deserializer)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = MessagePackEncoder(serializersModule, messagePacker)
        encoder.encodeSerializableValue(serializer, value)
        return encoder.buffer.toByteArray()
    }
}
