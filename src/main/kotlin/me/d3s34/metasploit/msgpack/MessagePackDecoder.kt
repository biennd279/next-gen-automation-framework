package me.d3s34.metasploit.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import me.d3s34.metasploit.msgpack.MessagePackType.Bin.isBinary
import me.d3s34.metasploit.msgpack.MessagePackType.String.isString
import java.nio.charset.Charset

interface PeekTypeMessagePackDecoder {
    fun peekTypeByte(): Byte
}

@ExperimentalSerializationApi
open class MessagePackDecoder(
    override val serializersModule: SerializersModule,
    private val buffer: InputMessageDataPacker
) : AbstractDecoder(), PeekTypeMessagePackDecoder {
    constructor(serializersModule: SerializersModule, byteArray: ByteArray) :
            this(serializersModule, InputMessageDataPacker(byteArray))

    private val messageUnpacker = MessageUnpacker(buffer)

    override fun peekTypeByte(): Byte {
        return buffer.peek()
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    override fun decodeNotNullMark(): Boolean {
        return peekTypeByte() == MessagePackType.NULL
    }

    override fun decodeNull(): Nothing? {
        messageUnpacker.unpackNull()
        return null
    }

    override fun decodeBoolean(): Boolean {
        return messageUnpacker.unpackBoolean()
    }

    override fun decodeByte(): Byte {
        return messageUnpacker.unpackInt().toByte()
    }

    override fun decodeChar(): Char {
        return Char(messageUnpacker.unpackInt().toInt())
    }

    override fun decodeInt(): Int {
        return messageUnpacker.unpackInt().toInt()
    }

    override fun decodeLong(): Long {
        return messageUnpacker.unpackInt().toLong()
    }

    override fun decodeShort(): Short {
        return messageUnpacker.unpackInt().toShort()
    }

    override fun decodeDouble(): Double {
        return messageUnpacker.unpackDouble()
    }

    override fun decodeFloat(): Float {
        return messageUnpacker.unpackFloat()
    }

    override fun decodeString(): String {
        return if (isString(peekTypeByte())) {
            messageUnpacker.unpackString()
        } else {
            //Some time it is bytearray
            messageUnpacker.unpackByteArray().toString(Charset.defaultCharset())
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(decodeString())
    }

    override fun decodeSequentially(): Boolean = true

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        val typeByte = buffer.requireNextByte()

        return when (descriptor.kind) {
            StructureKind.LIST -> {
                when {
                    MessagePackType.Array.FIXARRAY_SIZE_MASK.test(typeByte) ->
                        MessagePackType.Array.FIXARRAY_SIZE_MASK.unMaskValue(typeByte).toInt()
                    MessagePackType.Array.ARRAY16 == typeByte -> buffer.takeNext(2).toInt()
                    MessagePackType.Array.ARRAY32 == typeByte -> buffer.takeNext(4).toInt()
                    else ->
                        throw MessagePackDeserializeException("Unknown array type: ${typeByte.decodeHex()}")
                }
            }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP -> {
                when {
                    MessagePackType.Map.FIXMAP_SIZE_MASK.test(typeByte) ->
                        MessagePackType.Map.FIXMAP_SIZE_MASK.unMaskValue(typeByte).toInt()
                    MessagePackType.Map.MAP16 == typeByte -> buffer.takeNext(2).toInt()
                    MessagePackType.Map.MAP32 == typeByte -> buffer.takeNext(4).toInt()
                    else ->
                        throw MessagePackDeserializeException("Unknown object type: ${typeByte.decodeHex()}")
                }
            }

            else ->
                throw MessagePackDeserializeException("Unsupported collection: ${descriptor.kind}")
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            val size = decodeCollectionSize(descriptor)
            return MessagePackTreeDecoder(this, size)
        }

        return this
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal class MessagePackTreeDecoder(
    private val messagePackDecoder: MessagePackDecoder,
    private val size: Int
) : CompositeDecoder by messagePackDecoder, Decoder by messagePackDecoder, PeekTypeMessagePackDecoder by messagePackDecoder {
    @OptIn(ExperimentalSerializationApi::class)
    override val serializersModule: SerializersModule
        get() = messagePackDecoder.serializersModule

    var currentIndex = -1

    override fun decodeSequentially(): Boolean = false

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = size

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (currentIndex < size - 1) {
            currentIndex++

            val next = peekTypeByte()
            if (isString(next) || isBinary(next)) {

                val fieldName = kotlin.runCatching {
                    decodeString()
                }.getOrNull() ?: return CompositeDecoder.UNKNOWN_NAME

                return descriptor.getElementIndex(fieldName)
            }

            return CompositeDecoder.DECODE_DONE
        }
        return CompositeDecoder.DECODE_DONE
    }
}
