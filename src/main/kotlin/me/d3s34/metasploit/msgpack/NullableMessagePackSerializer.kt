package me.d3s34.metasploit.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer


open class MessagePackSerializer(
    private val nullableMessagePackSerializer: NullableMessagePackSerializer = NullableMessagePackSerializer()
    ): KSerializer<Any> {

    companion object Default: MessagePackSerializer()

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("MessagePack", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): Any {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: Any) {
        nullableMessagePackSerializer.serialize(encoder, value)
    }
}

open class NullableMessagePackSerializer() : KSerializer<Any?>{

    companion object Default: NullableMessagePackSerializer()

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("NullableMessagePack", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): Any? {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Any?) {
        require(encoder is MessagePackEncoder)

        @Suppress("UNCHECKED_CAST")
        when (value) {
            null -> encoder.encodeNull()
            ::isPrimitive -> encoder.encodeValue(value)
            is ByteArray -> encoder.encodeSerializableValue(ByteArraySerializer(), value)
            is List<*> -> ListSerializer(this).serialize(encoder, value.map { it })
            is Array<*> -> ArraySerializer(this).serialize(encoder, value.map { it }.toTypedArray())
            is Map<*, *> -> MapSerializer(this, this).serialize(encoder, value as Map<Any?, Any?>)
            is Map.Entry<*, *> ->
                MapEntrySerializer(this, this).serialize(encoder, value as Map.Entry<Any?, Any?>)
            else -> encoder.encodeSerializableValue(value::class.serializer() as KSerializer<Any>, value)
        }
    }

}