package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.zaproxy.addon.naf.model.CommixEngineType
import org.zaproxy.addon.naf.model.NafConfig
import org.zaproxy.addon.naf.model.NucleiEngineType
import org.zaproxy.addon.naf.model.SqlmapEngineType

fun ConfigTable.insert(nafConfig: NafConfig) = kotlin.run {
    insert {
        it[nucleiEngineType] = nafConfig.nucleiEngineType.name
        it[templateRootDir] = nafConfig.templateRootDir

        it[sqlmapEngineType] = nafConfig.sqlmapEngineType.name
        it[sqlmapApiUrl] = nafConfig.sqlmapApiUrl

        it[commixEngineType] = nafConfig.commixEngineType.name
    }
}

fun ConfigTable.update(nafConfig: NafConfig) = kotlin.run {
    update {
        it[nucleiEngineType] = nafConfig.nucleiEngineType.name
        it[templateRootDir] = nafConfig.templateRootDir ?: ""

        it[sqlmapEngineType] = nafConfig.sqlmapEngineType.name
        it[sqlmapApiUrl] = nafConfig.sqlmapApiUrl ?: ""

        it[commixEngineType] = nafConfig.commixEngineType.name
    }
}

fun ResultRow.toNafConfig() = NafConfig(
    nucleiEngineType = NucleiEngineType.valueOf(this[ConfigTable.nucleiEngineType]),
    templateRootDir = this[ConfigTable.templateRootDir],

    sqlmapEngineType = SqlmapEngineType.valueOf(this[ConfigTable.sqlmapEngineType]),
    sqlmapApiUrl = this[ConfigTable.sqlmapApiUrl],

    commixEngineType = CommixEngineType.valueOf(this[ConfigTable.commixEngineType])
)
