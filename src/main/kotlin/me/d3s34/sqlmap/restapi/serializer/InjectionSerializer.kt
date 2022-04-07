package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.d3s34.sqlmap.restapi.model.Injection
import me.d3s34.sqlmap.restapi.model.Technique

object InjectionSerializer: KSerializer<Injection> {

    override val descriptor: SerialDescriptor
        get() = Injection.serializer().descriptor

    private val format = Json { ignoreUnknownKeys = true }

    override fun deserialize(decoder: Decoder): Injection {
        require(decoder is JsonDecoder)

        val element = decoder.decodeJsonElement().jsonObject

        val techniques = format.decodeFromJsonElement(
            MapSerializer(String.serializer(), Technique.serializer()),
            element["data"]!!.jsonObject
        ).values.toList()

        return Injection(
            dbms = element["dbms"]!!.jsonPrimitive.content,
            parameter = element["parameter"]!!.jsonPrimitive.content,
            place = element["place"]!!.jsonPrimitive.content,
            techniques = techniques
        )
    }

    override fun serialize(encoder: Encoder, value: Injection) {
        Injection.serializer().serialize(encoder, value)
    }

}