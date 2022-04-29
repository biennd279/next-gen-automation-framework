package org.zaproxy.addon.naf.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.zaproxy.addon.naf.model.NafConfig
import org.zaproxy.addon.naf.model.NafIssue
import org.zaproxy.addon.naf.model.emptyConfig

class NafDatabase {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val sqlLogger: SqlLogger = StdOutSqlLogger

    fun connectAndMigrate() {
        log.info("Initialising database")
        val pool = hikari()
        Database.connect(pool)

        transaction {
            addLogger(sqlLogger)
            SchemaUtils.createMissingTablesAndColumns(IssueTable, ConfigTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
            .apply {
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:file:/tmp/test"
                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }

        return HikariDataSource(config)
    }

    fun loadConfig(): NafConfig = transaction {
        addLogger(StdOutSqlLogger)

        var lastConfig = ConfigTable.selectAll()
            .map { it.toNafConfig() }
            .firstOrNull()


        if (lastConfig == null) {
            ConfigTable.insert(emptyConfig)
            lastConfig = emptyConfig
        }

        return@transaction lastConfig
    }

    fun saveConfig(nafConfig: NafConfig) = transaction {
        addLogger(sqlLogger)

        ConfigTable.update(nafConfig)
    }


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
}
