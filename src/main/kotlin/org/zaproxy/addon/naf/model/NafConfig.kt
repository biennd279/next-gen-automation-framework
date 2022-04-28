package org.zaproxy.addon.naf.model

data class NafConfig(
    val nucleiEngineType: NucleiEngineType,
    val templateRootDir: String?,

    val sqlmapEngineType: SqlmapEngineType,
    val sqlmapApiUrl: String?,

    val commixEngineType: CommixEngineType
)

val home: String = System.getProperty("user.home")

internal val emptyConfig = NafConfig(
    NucleiEngineType.Native,
    "${home}/nuclei-templates/dns/",
    SqlmapEngineType.API,
    "http://127.0.0.1:8775/",
    CommixEngineType.DOCKER
)

fun emptyConfig() = emptyConfig


