package me.d3s34.sqlmap.restapi.request

import kotlinx.serialization.SerialName

//TODO: add more option
@kotlinx.serialization.Serializable
data class StartTaskRequest(
    val url: String,
    val data: String? = null,
    @SerialName("dump-all")
    val dumpAll: String? = null
)