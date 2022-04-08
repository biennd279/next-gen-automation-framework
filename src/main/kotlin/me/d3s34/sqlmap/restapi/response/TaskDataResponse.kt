package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.d3s34.sqlmap.restapi.content.Content
import me.d3s34.sqlmap.restapi.data.AbstractData
import me.d3s34.sqlmap.restapi.serializer.TaskDataResponseSerializer

@Serializable(
    with = TaskDataResponseSerializer::class
)
data class TaskDataResponse(
    @SerialName("data")
    val data: List<Content<AbstractData>>? = null,
    @SerialName("error")
    val error: List<String>? = null,
    @SerialName("success")
    val success: Boolean,
    @SerialName("message")
    val message: String? = null
)