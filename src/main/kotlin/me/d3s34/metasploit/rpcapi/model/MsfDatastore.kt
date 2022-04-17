package me.d3s34.metasploit.rpcapi.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.d3s34.lib.msgpack.MessagePackDecoder
import me.d3s34.lib.msgpack.MessagePackEncoder
import me.d3s34.lib.msgpack.MessagePackSerializer

@kotlinx.serialization.Serializable(
    with = MsfDatastoreSerializer::class
)
class MsfDatastore(
    _map: Map<String, Any>
): Map<String, Any> by _map

class MsfDatastoreSerializer: KSerializer<MsfDatastore> {
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("MsfDatastoreSerializer", StructureKind.MAP)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): MsfDatastore {
        require(decoder is MessagePackDecoder)

        @Suppress("UNCHECKED_CAST")
        val map = decoder.decodeSerializableValue(MessagePackSerializer) as Map<String, Any>

        return MsfDatastore(map)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: MsfDatastore) {
        require(encoder is MessagePackEncoder)

        encoder.encodeSerializableValue(MessagePackSerializer, value)
    }
}
