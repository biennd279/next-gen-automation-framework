package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.d3s34.sqlmap.restapi.content.Content

@Serializable
data class TaskDataResponse(
    @SerialName("data")
    val data: List<Content<*>>,
    @SerialName("error")
    val error: List<String>,
    @SerialName("success")
    val success: Boolean
)