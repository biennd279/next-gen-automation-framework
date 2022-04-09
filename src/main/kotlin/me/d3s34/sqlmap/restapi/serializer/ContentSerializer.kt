package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*
import me.d3s34.sqlmap.restapi.TaskDataContentType
import me.d3s34.sqlmap.restapi.content.Content
import me.d3s34.sqlmap.restapi.data.*


object ContentSerializer : JsonContentPolymorphicSerializer<Content<*>>(Content::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Content<*>> {
        return when (element.jsonObject["type"]!!.jsonPrimitive.intOrNull) {
            TaskDataContentType.TARGET.id -> Content.serializer(TargetData.serializer())
            TaskDataContentType.DUMP_TABLE.id -> Content.serializer(DumpTableContentSerializer)
            TaskDataContentType.TECHNIQUES.id -> Content.serializer(TechniqueData.serializer())
            TaskDataContentType.CURRENT_USER.id,
            TaskDataContentType.CURRENT_DB.id,
            TaskDataContentType.HOSTNAME.id -> Content.serializer(StringData.serializer())
            TaskDataContentType.DBS.id -> Content.serializer(ListStringData.serializer())
            TaskDataContentType.PRIVILEGES.id,
            TaskDataContentType.ROLES.id,
            TaskDataContentType.TABLES.id -> Content.serializer(MapStringListStringData.serializer())
            TaskDataContentType.COLUMNS.id,
            TaskDataContentType.SCHEMA.id -> Content.serializer(MapDbToTableToString.serializer())
            TaskDataContentType.COUNT.id -> Content.serializer(MapDbToCountToListTable.serializer())
            TaskDataContentType.SQL_QUERY.id -> Content.serializer(ListToListStringData.serializer())
            null -> error("Not found type data")
            else -> error("Not supported this type")
        }
    }
}
