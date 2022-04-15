package me.d3s34.lib.msgpack

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
import me.d3s34.lib.msgpack.MessagePackType.Array.isArray
import me.d3s34.lib.msgpack.MessagePackType.Bin.isBinary
import me.d3s34.lib.msgpack.MessagePackType.Boolean.isBoolean
import me.d3s34.lib.msgpack.MessagePackType.Float.isDouble
import me.d3s34.lib.msgpack.MessagePackType.Float.isFloat
import me.d3s34.lib.msgpack.MessagePackType.Int.isByte
import me.d3s34.lib.msgpack.MessagePackType.Int.isFixNum
import me.d3s34.lib.msgpack.MessagePackType.Int.isInt
import me.d3s34.lib.msgpack.MessagePackType.Int.isLong
import me.d3s34.lib.msgpack.MessagePackType.Int.isShort
import me.d3s34.lib.msgpack.MessagePackType.Map.isMap
import me.d3s34.lib.msgpack.MessagePackType.String.isString


open class MessagePackSerializer(
    private val nullableMessagePackSerializer: NullableMessagePackSerializer = NullableMessagePackSerializer()
    ): KSerializer<Any> {

    companion object Default: MessagePackSerializer()

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("MessagePack", SerialKind.CONTEXTUAL)

    override fun deserialize(decoder: Decoder): Any {
        return nullableMessagePackSerializer.deserialize(decoder)!!
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

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Any? {
        require(decoder is MessagePackDecoder)

        val typeByte = decoder.peekTypeByte()
        
        return when {
            isBoolean(typeByte) -> decoder.decodeBoolean()
            isFixNum(typeByte) || isByte(typeByte) -> decoder.decodeByte()
            isShort(typeByte) -> decoder.decodeShort()
            isInt(typeByte) -> decoder.decodeInt()
            isLong(typeByte) -> decoder.decodeLong()
            isFloat(typeByte) -> decoder.decodeFloat()
            isDouble(typeByte) -> decoder.decodeDouble()
//            isString(typeByte) || isBinary(typeByte) -> decoder.decodeString()
            isString(typeByte) -> decoder.decodeString()
            isBinary(typeByte) -> decoder.decodeSerializableValue(ByteArraySerializer())
            isArray(typeByte) -> ListSerializer(this).deserialize(decoder)
            isMap(typeByte) -> MapSerializer(this, this).deserialize(decoder)
            else ->
                throw MessagePackDeserializeException("Missing decoder for type: ${typeByte.decodeHex()}")
        }
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