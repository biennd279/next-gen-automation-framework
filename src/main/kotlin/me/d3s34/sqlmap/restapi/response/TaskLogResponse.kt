package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.d3s34.sqlmap.restapi.model.Log

@Serializable
data class TaskLogResponse(
    @SerialName("log")
    val log: List<Log>? = null,
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)