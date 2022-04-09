package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import me.d3s34.sqlmap.restapi.content.Content
import me.d3s34.sqlmap.restapi.data.AbstractData
import me.d3s34.sqlmap.restapi.response.TaskDataResponse
import org.slf4j.LoggerFactory

object TaskDataResponseSerializer: KSerializer<TaskDataResponse> {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val json = Json { ignoreUnknownKeys = true }

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor("TaskDataResponse") {
            element<Boolean>("success")
            element<List<String>?>("error")
            element<String?>("message")
            element<List<Content<AbstractData>>?>("data")
        }

    override fun deserialize(decoder: Decoder): TaskDataResponse {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement().jsonObject
        val data = element["data"]!!.jsonArray.mapNotNull {
            try {
                json.decodeFromJsonElement(ContentSerializer, it)
            } catch (t: Throwable) {
                println("$t")
                logger.warn("Found content but can not transform to obj: $it")
                null
            }
        }

        val error = element["error"]?.jsonArray?.let { json.decodeFromJsonElement<List<String>?>(it) }
        val message = element["message"]?.jsonPrimitive?.content
        val success = element["success"]!!.jsonPrimitive.boolean

        return TaskDataResponse(
            success = success,
            error = error,
            message = message,
            data = data
        )
    }

    override fun serialize(encoder: Encoder, value: TaskDataResponse) {
        TODO("Not yet implemented")
    }
}