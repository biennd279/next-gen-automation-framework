package me.d3s34.metasploit.rpcapi.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
abstract class MsfRpcResponse(
    val result: String = "",
    val error: Boolean = false,
    @SerialName("error_class")
    val errorClass: String = "",
    @SerialName("error_message")
    val errorMessage: String = "",
    @SerialName("error_string")
    val errorString: String = "",
    @SerialName("error_backtrace")
    val errorBacktrace: List<String>? = null
)
