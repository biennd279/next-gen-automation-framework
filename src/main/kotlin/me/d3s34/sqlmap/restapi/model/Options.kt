package me.d3s34.sqlmap.restapi.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//TODO standard input
@Serializable
data class Options(
    @SerialName("wizard")
    val wizard: Boolean? = null
)