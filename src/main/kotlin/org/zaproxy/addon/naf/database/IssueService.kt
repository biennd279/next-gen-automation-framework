package org.zaproxy.addon.naf.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.zaproxy.addon.naf.model.NafIssue

class IssueService {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val sqlLogger: SqlLogger = StdOutSqlLogger


    fun getAllIssue() = transaction {
        addLogger(sqlLogger)

        IssueTable.selectAll()
            .map { it.toNafIssue() }
    }

    fun saveNewIssue(nafIssue: NafIssue) = transaction {
        addLogger(sqlLogger)

        IssueTable.insert(nafIssue)
    }

    fun updateIssue(nafIssue: NafIssue) = transaction {
        addLogger(sqlLogger)

        IssueTable.update(nafIssue)
    }

    fun findIssue(id: Int) = transaction {
        addLogger(sqlLogger)

        IssueTable.findById(id)
    }

    fun removeIssue(id: Int) = transaction {
        addLogger(sqlLogger)

        IssueTable.deleteWhere { IssueTable.id eq id }
    }
}