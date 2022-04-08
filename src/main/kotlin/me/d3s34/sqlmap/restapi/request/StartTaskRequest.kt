package me.d3s34.sqlmap.restapi.request

//TODO: add more option
@kotlinx.serialization.Serializable
data class StartTaskRequest(
    //Data request
    val url: String,
    val data: String? = null,
    val cookie: String? = null,
    val randomAgent: Boolean = false,
    //Flag attack
    val level: Int = 1,
    val risk: Int = 1,
    val technique: String = "BEUSTQ",
    //Get current information
    val getHostname: Boolean = false,
    val getCurrentDb: Boolean = false,
    val getCurrentUser: Boolean = false,
    val getPrivileges: Boolean = false,
    val getRoles: Boolean = false,
    //Get database information
    val getDbs: Boolean = false,
    val getTables: Boolean = false,
    val getColumns: Boolean = false,
    val getSchema: Boolean = false,
    val getCount: Boolean = false,
    //Dump option
    val dumpAll: Boolean = false,
    val dumpTable: Boolean = false,
    val db: String? = null,
    val tbl: String? = null,
    val col: String? = null,
    //Exec query
    val sqlQuery: String? = null,
    //Sqlmap option
    val cleanup: Boolean = false,
    val updateAll: Boolean = false
) {
    init {
        require(level in 1..5 && risk in 1..3)
    }
}