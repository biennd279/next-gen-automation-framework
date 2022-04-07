package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*
import me.d3s34.sqlmap.restapi.content.Content
import me.d3s34.sqlmap.restapi.data.TargetData
import me.d3s34.sqlmap.restapi.model.ContentType


object ContentSerializer: JsonContentPolymorphicSerializer<Content<*>>(Content::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Content<*>> {
        return when(element.jsonObject["type"]!!.jsonPrimitive.intOrNull) {
            ContentType.TARGET.id -> Content.serializer(TargetData.serializer())
            ContentType.DUMP_TABLE.id -> Content.serializer(DumpTableSerializer)
            null -> error("Not found type data")
            else -> error("Not supported this type")
        }
    }
}
