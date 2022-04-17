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
import me.d3s34.metasploit.rpcapi.model.MsfThread
import me.d3s34.metasploit.rpcapi.response.MapResponse
import me.d3s34.metasploit.rpcapi.response.serializer.deserializeMap

@kotlinx.serialization.Serializable(
    with = ThreadListResponseSerializer::class
)
class ThreadListResponse(
    _map: MapResponse<Int, ThreadResponse>
): MapResponse<Int, ThreadResponse>(_map)

fun ThreadListResponse.toListThread(): List<MsfThread> {
    return buildList {
        this@toListThread.forEach { (id, response) ->
            add(
                MsfThread(
                    id = id,
                    status = response.status,
                    critical = response.critical,
                    name = response.name,
                    started = response.name
                )
            )
        }
    }
}

class ThreadListResponseSerializer: KSerializer<ThreadListResponse> {
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override val descriptor: SerialDescriptor
        get() = buildSerialDescriptor("ThreadListResponseSerializer", SerialKind.CONTEXTUAL)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): ThreadListResponse {
        require(decoder is MessagePackDecoder)

        val map = deserializeMap<Int, ThreadResponse>(decoder)

        return ThreadListResponse(map)
    }

    override fun serialize(encoder: Encoder, value: ThreadListResponse) {
        TODO("Not yet implemented")
    }
}
