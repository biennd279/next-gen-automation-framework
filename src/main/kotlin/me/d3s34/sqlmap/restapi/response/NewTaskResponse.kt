package me.d3s34.sqlmap.restapi.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewTaskResponse(
    @SerialName("success")
    val success: Boolean,
    @SerialName("taskid")
    val taskId: String
)