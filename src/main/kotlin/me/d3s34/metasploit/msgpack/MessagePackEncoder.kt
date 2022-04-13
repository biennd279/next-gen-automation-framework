package me.d3s34.metasploit.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

//Fork from package com.ensarsarajcic.kotlinx.serialization.msgpack
@ExperimentalSerializationApi
class MessagePackEncoder(
    override val serializersModule: SerializersModule,
    private val messagePacker: MessagePacker
): AbstractEncoder() {

    val buffer = MessageDataOutputPacker()

    //Encode Struct
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            return beginCollection(descriptor, descriptor.elementsCount)
        }
        return this
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        when (descriptor.kind) {
            StructureKind.LIST -> {
                when (collectionSize) {
                    in 0..MessagePackType.Array.MAX_FIXARRAY_SIZE -> {
                        buffer.add(MessagePackType.Array.FIXARRAY_SIZE_MASK.maskValue(collectionSize.toByte()))
                    }
                    in (MessagePackType.Array.MAX_FIXARRAY_SIZE + 1)..MessagePackType.Array.MAX_ARRAY16_LENGTH -> {
                        buffer.add(MessagePackType.Array.ARRAY16)
                        buffer.addAll(collectionSize.toShort().toByteArray())
                    }

                    in (MessagePackType.Array.MAX_ARRAY16_LENGTH + 1)..MessagePackType.Array.MAX_ARRAY32_LENGTH -> {
                        buffer.add(MessagePackType.Array.ARRAY32)
                        buffer.addAll(collectionSize.toByteArray())
                    }
                    else -> throw MessagePackSerializeException(
                        "Collection too long (max size = ${MessagePackType.Array.MAX_ARRAY32_LENGTH }, size = $collectionSize)!"
                    )
                }
            }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP -> {
                when (collectionSize) {
                    in 0..MessagePackType.Map.MAX_FIXMAP_SIZE -> {
                        buffer.add(MessagePackType.Map.FIXMAP_SIZE_MASK.maskValue(collectionSize.toByte()))
                    }
                    in (MessagePackType.Map.MAX_FIXMAP_SIZE + 1)..MessagePackType.Map.MAX_MAP16_LENGTH -> {
                        buffer.add(MessagePackType.Map.MAP16)
                        buffer.addAll(collectionSize.toShort().toByteArray())

                    }
                    in (MessagePackType.Map.MAX_MAP16_LENGTH + 1)..MessagePackType.Map.MAX_MAP32_LENGTH -> {
                        buffer.add(MessagePackType.Map.MAP32)
                        buffer.addAll(collectionSize.toByteArray())
                    }
                    else -> throw MessagePackSerializeException(
                        "Collection too long (max size = ${MessagePackType.Map.MAX_MAP32_LENGTH }, size = $collectionSize)!"
                    )
                }
            }

            else -> throw MessagePackSerializeException("Unsupported collection type: ${descriptor.kind}")
        }
        return this
    }

    //Encode value
    //TODO: encode inline
    override fun encodeNull() {
        buffer.addAll(messagePacker.packNull())
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        buffer.addAll(messagePacker.packString(enumDescriptor.getElementName(index)))
    }

    override fun encodeValue(value: Any): Unit {
        when(value) {
            is Boolean -> buffer.addAll(messagePacker.packBoolean(value))
            is Byte -> buffer.addAll(messagePacker.packByte(value))
            is Short -> buffer.addAll(messagePacker.packShort(value))
            is Int -> buffer.addAll(messagePacker.packInt(value))
            is Long -> buffer.addAll(messagePacker.packLong(value))
            is Float -> buffer.addAll(messagePacker.packFloat(value))
            is Double -> buffer.addAll(messagePacker.packDouble(value))
            is String -> buffer.addAll(messagePacker.packString(value))
            is Char -> buffer.addAll(messagePacker.packShort(value.code.toShort()))
            is ByteArray -> buffer.addAll(messagePacker.packByteArray(value))
            else -> throw SerializationException("Non-serializable ${value::class} is not supported by ${this::class} encoder")
        }
    }

    //TODO: encode inline element
    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            encodeString(descriptor.getElementName(index))
        }
        return true
    }
}