package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class ListToListStringData(val value: List<List<String>>) : AbstractData