package me.d3s34.sqlmap.restapi.data

@kotlinx.serialization.Serializable
class DumpTableData(
    val data: HashMap<String, List<String>>,
    val table: String,
    val db: String
): AbstractData, Map<String, List<String>> by data
