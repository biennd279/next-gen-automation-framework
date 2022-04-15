package me.d3s34.sqlmap.restapi.data

import me.d3s34.sqlmap.restapi.model.Injection

@kotlinx.serialization.Serializable
@JvmInline
value class TechniqueData(val value: List<Injection>) : AbstractData
