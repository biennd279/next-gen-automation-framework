package me.d3s34.metasploit.rpcapi.response

import me.d3s34.metasploit.rpcapi.emptyResponse

class ListResponse<T>(
    private val isError: Boolean = false,
    val list: List<T> = emptyList(),
    val response: MsfRpcResponse = emptyResponse()
): MsfRpcResponse(response), List<T> by list