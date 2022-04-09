package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class StringData(val value: String): AbstractData
