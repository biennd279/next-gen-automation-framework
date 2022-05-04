package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.Table

object ConfigTable: Table("config") {
    val nucleiEngineType = varchar("nuclei_engine", 50)
    val templateRootDir = varchar("template_root_dir", 250).nullable()

    val sqlmapEngineType = varchar("sqlmap_engine_type", 50)
    val sqlmapApiUrl = varchar("sqlmap_api_url", 250).nullable()

//    val commixEngineType = varchar("commix_engine_type", 50)
//    val tplmapEngineType = varchar("tplmap_engine_type", 50)
}
