package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.Table

object IssueTable: Table(name = "issues") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 250)
    val severity = varchar("severity", 50)
    val description = text("description")
    val reproduce = text("reproduce")
    val solution = text("solution")
    val note = text("note")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}
