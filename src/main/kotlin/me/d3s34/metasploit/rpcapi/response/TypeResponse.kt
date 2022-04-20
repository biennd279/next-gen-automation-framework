package me.d3s34.metasploit.rpcapi.response

import me.d3s34.metasploit.rpcapi.emptyResponse

class TypeResponse<T>(
    private val isError: Boolean = false,
    val value: T? = null,
    val response: MsfRpcResponse = emptyResponse()
): MsfRpcResponse(response)
