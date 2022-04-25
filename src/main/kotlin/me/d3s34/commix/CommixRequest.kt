package me.d3s34.commix

data class CommixRequest(
    val url: String,
    val data: String? = null,
    val cookies: String? = null,
    val randomAgent: Boolean = false
)
