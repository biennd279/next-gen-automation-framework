package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.json.*
import me.d3s34.sqlmap.restapi.data.DumpTableData

object DumpTableDataSerializer: JsonTransformingSerializer<DumpTableData>(DumpTableData.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val elements =  element
            .jsonObject
            .filter { it.key != "__infos__" }
            .mapValues {
                it.value.jsonObject["values"]!!.jsonArray
            }
        val table = element.jsonObject["__infos__" ]!!.jsonObject["table"]!!.jsonPrimitive
        val db = element.jsonObject["__infos__" ]!!.jsonObject["db"]!!.jsonPrimitive

        return buildJsonObject {
            put("table", table)
            put("db", db)
            put("data", JsonObject(elements))
        }
    }
}

