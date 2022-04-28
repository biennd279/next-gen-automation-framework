package org.zaproxy.addon.naf.model

data class NafConfig(
    val nucleiEngineType: NucleiEngineType,
    val templateRootDir: String?,

    val sqlmapEngineType: SqlmapEngineType,
    val sqlmapApiUrl: String?,

    val commixEngineType: CommixEngineType
)

internal val emptyConfig = NafConfig(
    NucleiEngineType.None,
    null,
    SqlmapEngineType.NONE,
    null,
    CommixEngineType.NONE
)

fun emptyConfig() = emptyConfig
