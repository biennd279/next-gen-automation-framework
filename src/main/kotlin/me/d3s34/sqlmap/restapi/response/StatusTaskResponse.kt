package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusTaskResponse(
    @SerialName("returncode")
    val returnCode: Int,
    @SerialName("status")
    val status: String,
    @SerialName("success")
    val success: Boolean
)