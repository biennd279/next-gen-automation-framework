package me.d3s34.metasploit.rpcapi.response

import me.d3s34.metasploit.rpcapi.emptyResponse

@kotlinx.serialization.Serializable
open class MapResponse<T, U>(
    val isError: Boolean = false,
    val map: Map<T, U> = emptyMap(),
    val response: MsfRpcResponse = emptyResponse()
): MsfRpcResponse(response), Map<T, U> by map {
    constructor(
        mapResponse: MapResponse<T, U>
    ): this(
        isError = mapResponse.isError,
        map = mapResponse.map,
        response = mapResponse.response
    )
}