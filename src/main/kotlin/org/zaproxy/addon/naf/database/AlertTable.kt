package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.Table

object AlertTable: Table("alerts") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 250)
    val uri = varchar("uri", 500)
    val param = varchar("param", 50)
    val risk = integer("risk")
    val confidence = integer("confidence")
    val alertSource = integer("source")
    val cweId = integer("cwe_id")
    val description = text("description")
    val solution = text("solution")
    val otherInfo = text("other_info")
}
