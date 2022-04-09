package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class ListStringData(val value: List<String>): AbstractData
