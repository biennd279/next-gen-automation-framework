package org.zaproxy.addon.naf.model

import java.net.URL

data class LfiRequest(
    val url: URL,
    val param: String,
    val vectorAttack: String,
    val filePath: String,
    val data: String? = null,
    val cookie: String = "",
)
