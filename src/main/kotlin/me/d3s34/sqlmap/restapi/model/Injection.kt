package me.d3s34.sqlmap.restapi.model

import me.d3s34.sqlmap.restapi.serializer.InjectionSerializer

@kotlinx.serialization.Serializable(
    with = InjectionSerializer::class
)
data class Injection(
    val dbms: String,
    val parameter: String,
    val place: String,
    val techniques: List<Technique> //`data` obj but in list
): List<Technique> by techniques

@kotlinx.serialization.Serializable
data class Technique(
    val payload: String,
    val title: String
)
