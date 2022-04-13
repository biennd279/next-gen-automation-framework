package me.d3s34.metasploit.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class MessageEncoder(
    override val serializersModule: SerializersModule,
    private val messagePacker: MessagePacker
): AbstractEncoder() {

    private val buffer = MessageDataOutputPacker()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            return beginCollection(descriptor, descriptor.elementsCount)
        }

        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        //Nop
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        when (descriptor.kind) {
            StructureKind.LIST -> {
                when (collectionSize) {
                    in 0..MessagePackType.Array.MAX_FIXARRAY_SIZE -> {

                    }
                    in (MessagePackType.Array.MAX_FIXARRAY_SIZE + 1)..MessagePackType.Array.MAX_ARRAY16_LENGTH -> {

                    }

                    in (MessagePackType.Array.MAX_ARRAY16_LENGTH + 1)..MessagePackType.Array.MAX_ARRAY32_LENGTH -> {

                    }
                    else -> {

                    }
                }
            }

            StructureKind.CLASS,
            StructureKind.OBJECT -> TODO()

            StructureKind.MAP -> TODO()
            else -> {
                TODO()
            }
        }
        return this
    }

    override fun encodeNull() {
        buffer.addAll(messagePacker.packNull())
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        buffer.addAll(messagePacker.packString(enumDescriptor.getElementName(index)))
    }

    fun encodeByteArray(value: ByteArray): Unit {
        buffer.addAll(messagePacker.packByteArray(value))
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
            else -> super.encodeValue(value)
        }
    }

    private fun encodeName(descriptor: SerialDescriptor, index: Int) {
        encodeString(descriptor.getElementName(index))
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
//        if (descriptor.kind is PrimitiveKind) {
//            encodeName(descriptor, index)
//        }
        encodeName(descriptor, index)
        return true
    }

}