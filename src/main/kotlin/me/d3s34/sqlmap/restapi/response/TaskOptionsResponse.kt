package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.d3s34.sqlmap.restapi.model.Options

@Serializable
data class TaskOptionsResponse(
    @SerialName("options")
    val options: Options? = null,
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)