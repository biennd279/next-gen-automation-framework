package me.d3s34.sqlmap.restapi.data

import me.d3s34.sqlmap.restapi.model.Injection

@kotlinx.serialization.Serializable
data class TechniqueData(
    val list: List<Injection>
): AbstractData, List<Injection> by list
