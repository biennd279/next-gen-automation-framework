package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KillTaskResponse(
    @SerialName("message")
    val message: String,
    @SerialName("success")
    val success: Boolean
)