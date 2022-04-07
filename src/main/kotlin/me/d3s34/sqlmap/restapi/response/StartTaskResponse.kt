package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StartTaskResponse(
    @SerialName("engineid")
    val engineId: Int,
    @SerialName("success")
    val success: Boolean
)