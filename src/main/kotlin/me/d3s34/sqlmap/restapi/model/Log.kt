package me.d3s34.sqlmap.restapi.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Log(
    @SerialName("level")
    val level: String,
    @SerialName("message")
    val message: String,
    @SerialName("time")
    val time: String
)