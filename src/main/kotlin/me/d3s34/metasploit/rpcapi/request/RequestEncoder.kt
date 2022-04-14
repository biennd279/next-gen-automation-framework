package me.d3s34.metasploit.rpcapi.request

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
class RequestEncoder(
    override val serializersModule: SerializersModule,
    private val encoder: Encoder = NoOpEncoder,
): CompositeEncoder {
    private val mapIndexName: MutableMap<Int, String> = mutableMapOf()
    private val mapNameValue: MutableMap<String, Any?> = mutableMapOf()

    fun <T> getListByRightOrder(
        value: T,
        filterBy: (name: String) -> Boolean
    ): List<Any?> {

        mapIndexName.clear()
        mapNameValue.clear()

        val nameByOrder = mapIndexName.keys.sorted().map { mapIndexName[it]!! }
            .filter { filterBy(it) }

        return nameByOrder.map { mapNameValue[it] }
    }

    private fun encodeElement(descriptor: SerialDescriptor, index: Int, value: Any?) {
        val name = descriptor.getElementName(index)
        mapIndexName[index] = descriptor.getElementName(index)
        mapNameValue[name] = value
    }

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) =
        encodeElement(descriptor, index, value)

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) =
        encodeElement(descriptor, index, value)

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char)=
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

    override fun endStructure(descriptor: SerialDescriptor) { }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?
    ) {
        val isNullabilitySupported = serializer.descriptor.isNullable
        if (isNullabilitySupported) {
            @Suppress("UNCHECKED_CAST")
            encoder.encodeSerializableValue(serializer as SerializationStrategy<T?>, value)
        }

        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeNotNullMark()
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        serializer.serialize(encoder, value)
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal object NoOpEncoder : AbstractEncoder() {
    override val serializersModule: SerializersModule = EmptySerializersModule

    public override fun encodeValue(value: Any): Unit = Unit

    override fun encodeNull(): Unit = Unit

    override fun encodeBoolean(value: Boolean): Unit = Unit
    override fun encodeByte(value: Byte): Unit = Unit
    override fun encodeShort(value: Short): Unit = Unit
    override fun encodeInt(value: Int): Unit = Unit
    override fun encodeLong(value: Long): Unit = Unit
    override fun encodeFloat(value: Float): Unit = Unit
    override fun encodeDouble(value: Double): Unit = Unit
    override fun encodeChar(value: Char): Unit = Unit
    override fun encodeString(value: String): Unit = Unit
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int): Unit = Unit
}

fun AbstractRequest.encodeRequestToList(
    serializersModule: SerializersModule
): List<Any?> {
    val encoder = RequestEncoder(serializersModule)

    val payload = encoder.getListByRightOrder(this) { name ->
        name != group && name != method
    }

    return buildList {
        add("${group}.${method}")
        addAll(payload)
    }
}
