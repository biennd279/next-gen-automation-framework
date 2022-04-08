package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StopTaskResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)