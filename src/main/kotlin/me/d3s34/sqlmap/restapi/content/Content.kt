package me.d3s34.sqlmap.restapi.content

import me.d3s34.sqlmap.restapi.data.AbstractData

@kotlinx.serialization.Serializable
data class Content<out T : AbstractData>(
    val status: Int,
    val type: Int,
    val value: T
)

//Format Json wrap Content wrap Data
