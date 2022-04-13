package me.d3s34.metasploit.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import me.d3s34.metasploit.msgpack.MessagePackType.Array.isArray
import me.d3s34.metasploit.msgpack.MessagePackType.Bin.isBinary
import me.d3s34.metasploit.msgpack.MessagePackType.Boolean.isBoolean
import me.d3s34.metasploit.msgpack.MessagePackType.Float.isDouble
import me.d3s34.metasploit.msgpack.MessagePackType.Float.isFloat
import me.d3s34.metasploit.msgpack.MessagePackType.Int.isByte
import me.d3s34.metasploit.msgpack.MessagePackType.Int.isInt
import me.d3s34.metasploit.msgpack.MessagePackType.Int.isLong
import me.d3s34.metasploit.msgpack.MessagePackType.Int.isShort
import me.d3s34.metasploit.msgpack.MessagePackType.String.isString
import kotlin.reflect.KClass

@ExperimentalSerializationApi
class MessagePackDecoder(
    override val serializersModule: SerializersModule,
    private val buffer: InputMessageDataPacker
) : AbstractDecoder() {
    constructor(serializersModule: SerializersModule, byteArray: ByteArray):
            this(serializersModule, InputMessageDataPacker(byteArray))

    private val messageUnpacker = MessageUnpacker(buffer)

    fun peekTypeByte(): Byte {
        return buffer.peek()
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val next = buffer.peekSafely()
        if (next == null || !isString(next)) return CompositeDecoder.DECODE_DONE

        val fieldName = kotlin.runCatching { decodeString() }
            .getOrNull() ?: return CompositeDecoder.UNKNOWN_NAME

        val index = descriptor.getElementIndex(fieldName)

        if (index == CompositeDecoder.UNKNOWN_NAME) {
            TODO()
        }

        return index
    }

    override fun decodeNotNullMark(): Boolean {
        return peekTypeByte() == MessagePackType.NULL
    }

    override fun decodeNull(): Nothing? {
        messageUnpacker.unpackNull()
        return null
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return enumDescriptor.getElementIndex(decodeString())
    }

    override fun decodeValue(): Any {
        val typeByte = peekTypeByte()

        return when {
            isBoolean(typeByte) -> messageUnpacker.unpackBoolean()
            isByte(typeByte) -> messageUnpacker.unpackByte()
            isShort(typeByte) -> messageUnpacker.unpackShort()
            isInt(typeByte) -> messageUnpacker.unpackInt()
            isLong(typeByte) -> messageUnpacker.unpackLong()
            isFloat(typeByte) -> messageUnpacker.unpackFloat()
            isDouble(typeByte) -> messageUnpacker.unpackDouble()
            isString(typeByte) -> messageUnpacker.unpackString()
            isBinary(typeByte) -> messageUnpacker.unpackByteArray()
            else -> throw MessagePackDeserializeException()
        }
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
                    else -> throw MessagePackDeserializeException("Unknown array type: $typeByte")
                }
            }

            StructureKind.CLASS, StructureKind.OBJECT, StructureKind.MAP -> {
                when {
                    MessagePackType.Map.FIXMAP_SIZE_MASK.test(typeByte) ->
                        MessagePackType.Array.FIXARRAY_SIZE_MASK.unMaskValue(typeByte).toInt()
                    MessagePackType.Map.MAP16 == typeByte -> buffer.takeNext(2).toInt()
                    MessagePackType.Map.MAP32 == typeByte -> buffer.takeNext(4).toInt()
                    else -> throw MessagePackDeserializeException("Unknown object type: $typeByte")
                }
            }

            else -> throw MessagePackDeserializeException("Unsupported collection: ${descriptor.kind}")
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (descriptor.kind in arrayOf(StructureKind.CLASS, StructureKind.OBJECT)) {
            decodeElementIndex(descriptor)
        }

        return this
    }
}
