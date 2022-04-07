package me.d3s34.sqlmap.restapi.data

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TargetData(
    @SerialName("url")
    val url: String,
    @SerialName("query")
    val query: String? = null,
    @SerialName("data")
    val data: String? = null,
): AbstractData
