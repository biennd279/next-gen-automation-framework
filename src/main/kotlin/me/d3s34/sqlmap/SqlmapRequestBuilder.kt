package me.d3s34.sqlmap

import me.d3s34.sqlmap.restapi.request.StartTaskRequest

class SqlmapRequestBuilder {
    //Data request
    var url: String? = null
    var data: String? = null
    //Get current information
    var getHostname: Boolean = false
    var getCurrentDb: Boolean = false
    var getCurrentUser: Boolean = false
    var getPrivileges: Boolean = false
    var getRoles: Boolean = false
    //Get database information
    var getDbs: Boolean = false
    var getTables: Boolean = false
    var getColumns: Boolean = false
    var getSchema: Boolean = false
    var getCount: Boolean = false
    //Dump option
    var dumpAll: Boolean = false
    var dumpTable: Boolean = false
    var db: String? = null
    var tbl: String? = null
    var col: String? = null

    //
    var cookie: String? = null
    var randomAgent: Boolean = false
    //Flag attack
    var level: Int = 1
    var risk: Int = 1
    var technique: String? = null

    var freshQueries: Boolean = false

    //Exec query
    var sqlQuery: String? = null
    //Sqlmap option
    var cleanup: Boolean = false
    var updateAll: Boolean = false


    fun build(): StartTaskRequest {

        require(cleanup || updateAll || url != null) {
            "Require either cleanup, update or attack request"
        }

        return StartTaskRequest(
            url,
            data,
            getHostname,
            getCurrentDb,
            getCurrentUser,
            getPrivileges,
            getRoles,
            getDbs,
            getTables,
            getColumns,
            getSchema,
            getCount,
            dumpAll,
            dumpTable,
            db,
            tbl,
            col,
            cookie,
            randomAgent,
            level,
            risk,
            technique,
            freshQueries,
            sqlQuery,
            cleanup,
            updateAll,
        )
    }
}

fun startTaskRequest(lambda: SqlmapRequestBuilder.() -> Unit) = SqlmapRequestBuilder()
    .apply(lambda)
    .build()
