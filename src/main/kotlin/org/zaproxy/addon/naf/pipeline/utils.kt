package org.zaproxy.addon.naf.pipeline

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.urlEncode(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.name())

fun String.urlDecode(): String = URLDecoder.decode(this, StandardCharsets.UTF_8.name())
