package me.d3s34.metasploit.rpcapi.response

import me.d3s34.metasploit.rpcapi.emptyResponse

open class MapOfMapResponse<T, U, V>(
    private val isError: Boolean = false,
    val map: Map<T, Map<U, V>> = emptyMap(),
    val response: MsfRpcResponse = emptyResponse()
): MsfRpcResponse(response), Map<T, Map<U, V>> by map{
    constructor(
        mapOfMapResponse: MapOfMapResponse<T, U, V>
    ): this(
        isError = mapOfMapResponse.isError,
        map = mapOfMapResponse.map,
        response = mapOfMapResponse.response
    )
}