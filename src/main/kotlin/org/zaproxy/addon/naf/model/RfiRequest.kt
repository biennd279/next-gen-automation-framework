package org.zaproxy.addon.naf.model

import java.net.URL

data class RfiRequest(
    val url: URL?,
    val param: String,
    val remoteFileInclude: String,
    val data: String? = null,
    val cookie: String? = null,
    val command: String? = null
)

fun emptyRfiRequest() = RfiRequest(
    null,
    "",
    "",
    null,
    null,
    null
)
