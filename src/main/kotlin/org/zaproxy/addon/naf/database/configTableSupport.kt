package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.zaproxy.addon.naf.model.*

fun ConfigTable.insert(nafConfig: NafConfig) = kotlin.run {
    insert {
        it[nucleiEngineType] = nafConfig.nucleiEngineType.name
        it[templateRootDir] = nafConfig.templateRootDir

        it[sqlmapEngineType] = nafConfig.sqlmapEngineType.name
        it[sqlmapApiUrl] = nafConfig.sqlmapApiUrl

//        it[commixEngineType] = nafConfig.commixEngineType.name
//        it[tplmapEngineType] = nafConfig.tplmapEngineType.name
    }
}

fun ConfigTable.update(nafConfig: NafConfig) = kotlin.run {
    update {
        it[nucleiEngineType] = nafConfig.nucleiEngineType.name
        it[templateRootDir] = nafConfig.templateRootDir ?: ""

        it[sqlmapEngineType] = nafConfig.sqlmapEngineType.name
        it[sqlmapApiUrl] = nafConfig.sqlmapApiUrl ?: ""

//        it[commixEngineType] = nafConfig.commixEngineType.name
//        it[tplmapEngineType] = nafConfig.tplmapEngineType.name
    }
}

fun ResultRow.toNafConfig() = NafConfig(
    nucleiEngineType = NucleiEngineType.valueOf(this[ConfigTable.nucleiEngineType]),
    templateRootDir = this[ConfigTable.templateRootDir],

    sqlmapEngineType = SqlmapEngineType.valueOf(this[ConfigTable.sqlmapEngineType]),
    sqlmapApiUrl = this[ConfigTable.sqlmapApiUrl],

    commixEngineType = CommixEngineType.DOCKER,
    tplmapEngineType = TplmapEngineType.DOCKER
)
