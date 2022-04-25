package org.zaproxy.addon.naf

interface NafServiceStatus {
    val isNucleiAvailable: Boolean
    val isSqlmapAvailable: Boolean
}
