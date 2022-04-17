package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
@JvmInline
value class MapDbToCountToListTable(val value: Map<String, Map<String, List<String>>>) : AbstractData
