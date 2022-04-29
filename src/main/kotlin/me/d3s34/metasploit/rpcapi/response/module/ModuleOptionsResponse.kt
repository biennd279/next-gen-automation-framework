package me.d3s34.metasploit.rpcapi.response.module

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.d3s34.lib.msgpack.MessagePackDecoder
import me.d3s34.metasploit.rpcapi.response.MapOfMapResponse
import me.d3s34.metasploit.rpcapi.response.serializer.deserializeMapOfMap

@kotlinx.serialization.Serializable(
    with = ModuleOptionsSerializer::class
)
class ModuleOptionsResponse(
    _map: MapOfMapResponse<String, String, Any>
): MapOfMapResponse<String, String, Any>(_map)

class ModuleOptionsSerializer(): KSerializer<ModuleOptionsResponse> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("ModuleOptionsResponse", SerialKind.CONTEXTUAL)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ModuleOptionsResponse {
        require(decoder is MessagePackDecoder)

        val map = deserializeMapOfMap<String, String, Any>(decoder)

        return ModuleOptionsResponse(map)
    }

    override fun serialize(encoder: Encoder, value: ModuleOptionsResponse) {
        TODO("Not yet implemented")
    }
}
