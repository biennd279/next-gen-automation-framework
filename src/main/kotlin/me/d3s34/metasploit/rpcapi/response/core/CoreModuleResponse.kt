package me.d3s34.metasploit.rpcapi.response.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import me.d3s34.lib.msgpack.MessagePackDecoder
import me.d3s34.metasploit.rpcapi.response.MapResponse
import me.d3s34.metasploit.rpcapi.response.serializer.deserializeMap

@kotlinx.serialization.Serializable(
    with = CoreModuleResponseSerializer::class
)
class CoreModuleResponse(
    _map: MapResponse<String, Int>
): MapResponse<String, Int>(_map)

class CoreModuleResponseSerializer: KSerializer<CoreModuleResponse> {
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("CoreModuleResponse", SerialKind.CONTEXTUAL)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): CoreModuleResponse {
        require(decoder is MessagePackDecoder)

        val map = deserializeMap<String, Int>(decoder)

        return CoreModuleResponse(map)
    }

    override fun serialize(encoder: Encoder, value: CoreModuleResponse) {
        TODO("Not yet implemented")
    }
}
