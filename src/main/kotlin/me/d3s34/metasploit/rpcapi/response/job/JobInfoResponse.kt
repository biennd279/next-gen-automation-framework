package me.d3s34.metasploit.rpcapi.response.job

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

class JobInfoResponse(
    _map: MapResponse<String, Any>
): MapResponse<String, Any>(_map)

class JobInfoResponseSerializer(): KSerializer<JobInfoResponse> {
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("JobInfoResponse", SerialKind.CONTEXTUAL)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): JobInfoResponse {
        require(decoder is MessagePackDecoder)

        val map = deserializeMap<String, Any>(decoder)

        return JobInfoResponse(map)
    }

    override fun serialize(encoder: Encoder, value: JobInfoResponse) {
        TODO("Not yet implemented")
    }
}
