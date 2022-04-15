package me.d3s34.metasploit.rpcapi.request

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.properties.Properties
import kotlinx.serialization.serializer

@OptIn(ExperimentalSerializationApi::class)
class RequestEncoder(
    override val serializersModule: SerializersModule = EmptySerializersModule,
): Encoder, CompositeEncoder {
    private val mapIndexName: MutableMap<Int, String> = mutableMapOf()
    private val mapNameValue: MutableMap<String, Any?> = mutableMapOf()

    fun getListByRightOrder(
        filterBy: (name: String) -> Boolean = { name ->
            name != "group" && name != "method"
        }
    ): List<Any?> {

        val nameByOrder = mapIndexName
            .keys
            .sorted()
            .map { mapIndexName[it]!! }
            .filter { filterBy(it) }

        return nameByOrder.map { mapNameValue[it] }
    }

    private fun encodeElement(descriptor: SerialDescriptor, index: Int, value: Any?) {
        val name = descriptor.getElementName(index)
        mapIndexName[index] = descriptor.getElementName(index)
        mapNameValue[name] = value
    }


    override fun encodeBoolean(value: Boolean) = Unit

    override fun encodeByte(value: Byte) = Unit

    override fun encodeChar(value: Char) = Unit

    override fun encodeDouble(value: Double) = Unit

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = Unit

    override fun encodeFloat(value: Float) = Unit

    @ExperimentalSerializationApi
    override fun encodeInline(inlineDescriptor: SerialDescriptor): Encoder {
        TODO("Not yet implemented")
    }

    override fun encodeInt(value: Int) = Unit

    override fun encodeLong(value: Long) = Unit

    @ExperimentalSerializationApi
    override fun encodeNull() = Unit

    override fun encodeShort(value: Short) = Unit

    override fun encodeString(value: String) = Unit

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        encodeElement(descriptor, index, value)

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        encodeElement(descriptor, index, value)

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) =
        encodeElement(descriptor, index, value)

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) =
        encodeElement(descriptor, index, value)

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) =
        encodeElement(descriptor, index, value)

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) =
        encodeElement(descriptor, index, value)

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) =
        encodeElement(descriptor, index, value)

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) =
        encodeElement(descriptor, index, value)

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) =
        encodeElement(descriptor, index, value)

    @ExperimentalSerializationApi
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        TODO("Not yet implemented")
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return this
    }
    override fun endStructure(descriptor: SerialDescriptor) {}

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        encodeElement(descriptor, index, value)
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        encodeNullableSerializableElement(descriptor, index, serializer, value)
    }
}
