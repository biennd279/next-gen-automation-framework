package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.d3s34.sqlmap.restapi.data.AbstractData

@Serializable
data class TaskDataResponse(
    @Contextual()
    @SerialName("data")
    val data: List<AbstractData>,
    @SerialName("error")
    val error: List<String?>,
    @SerialName("success")
    val success: Boolean
)