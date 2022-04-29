package me.d3s34.metasploit.rpcapi.response.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.serializer
import me.d3s34.lib.msgpack.MessagePackDecoder
import me.d3s34.lib.msgpack.MessagePackSerializer
import me.d3s34.metasploit.rpcapi.emptyResponse
import me.d3s34.metasploit.rpcapi.response.*

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun  <reified T: Any, reified U: Any> deserializeMap(decoder: MessagePackDecoder): MapResponse<T, U> {
    var isError = false
    var response = emptyResponse<MsfRpcResponse>()

    val map = runCatching {
        @Suppress("UNCHECKED_CAST")
        (decoder
            .tryDecodeSerializableValue(MapSerializer(T::class.serializer(), U::class.serializer())))
    }
        .onFailure {
            isError = true
            response = decoder.decodeSerializableValue(MsfRpcResponse::class.serializer())
        }
        .getOrDefault(emptyMap())


    return MapResponse(isError, map, response)
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun  <reified T: Any, reified U: Any, reified V: Any> deserializeMapOfMap(decoder: MessagePackDecoder): MapOfMapResponse<T, U, V> {
    var isError = false
    var response = emptyResponse<MsfRpcResponse>()

    val map = runCatching {
        @Suppress("UNCHECKED_CAST")
        (decoder
            .tryDecodeSerializableValue(
                MapSerializer(
                    T::class.serializer(),
                    MapSerializer(U::class.serializer(), MessagePackSerializer)
                )
            )) as Map<T, Map<U, V>>
    }
        .onFailure {
            println("Error at decode MapToMap $it")
            isError = true
            response = decoder.decodeSerializableValue(MsfRpcResponse::class.serializer())
        }
        .getOrDefault(emptyMap())


    return MapOfMapResponse(isError, map, response)
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun <reified T: Any> deserializeList(decoder: MessagePackDecoder): ListResponse<T> {
    var isError = false
    var response = emptyResponse<MsfRpcResponse>()

    val list = kotlin.runCatching {
        @Suppress("UNCHECKED_CAST")
        (decoder.tryDecodeSerializableValue(ListSerializer(T::class.serializer())))
    }
        .onFailure {
            isError = true
            response = decoder.decodeSerializableValue(MsfRpcResponse::class.serializer())
        }
        .getOrDefault(emptyList())

    return ListResponse(isError, list, response)
}

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
inline fun <reified T: Any> deserializeType(decoder: MessagePackDecoder): TypeResponse<T> {
    var isError = false
    var response = emptyResponse<MsfRpcResponse>()

    val value = kotlin.runCatching {
        @Suppress("UNCHECKED_CAST")
        decoder.tryDecodeSerializableValue(T::class.serializer())
    }
        .onFailure {
            isError = true
            response = decoder.decodeSerializableValue(MsfRpcResponse::class.serializer())
        }
        .getOrDefault(null)

    return TypeResponse(isError, value, response)
}
