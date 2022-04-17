package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class MapStringListStringData(val value: Map<String, List<String>>) : AbstractData
