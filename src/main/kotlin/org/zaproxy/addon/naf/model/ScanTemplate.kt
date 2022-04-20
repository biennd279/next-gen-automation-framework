package org.zaproxy.addon.naf.model


data class ScanTemplate(
    val url: String
)

fun emptyTemplate(): ScanTemplate = ScanTemplate("")
