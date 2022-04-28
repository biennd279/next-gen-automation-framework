package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.Severity

fun mapToNafIssue(it: ResultRow) = NafIssue(
    id = it[IssueTable.id],
    name = it[IssueTable.name],
    severity = Severity.valueOf(it[IssueTable.severity]),
    reproduce = it[IssueTable.reproduce],
    solution = it[IssueTable.solution],
    note = it[IssueTable.note],
    description = it[IssueTable.description]
)

fun ResultRow.toNafIssue() = mapToNafIssue(this)

fun IssueTable.insert(nafIssue: NafIssue) = run {
    this.insert {
        it[name] = nafIssue.name
        it[severity] = nafIssue.severity.name
        it[reproduce] = nafIssue.reproduce
        it[solution] = nafIssue.solution
        it[note] = nafIssue.note
        it[description] = nafIssue.description
    }
}

fun IssueTable.findById(id: Int) = kotlin.run {
    this.select { IssueTable.id eq id }
        .map { mapToNafIssue(it) }
        .firstOrNull()
}
