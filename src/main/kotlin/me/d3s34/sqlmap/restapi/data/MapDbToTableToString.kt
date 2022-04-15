package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class MapDbToTableToString(val value: Map<String, Map<String, Map<String, String>>>) : AbstractData
