package me.d3s34.sqlmap.restapi.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import me.d3s34.sqlmap.restapi.data.TechniqueData

object TechniqueDataSerializer: KSerializer<TechniqueData> {
    override val descriptor: SerialDescriptor
        get() = TechniqueData.serializer().descriptor

    private val format = Json { ignoreUnknownKeys = true }

    override fun deserialize(decoder: Decoder): TechniqueData {
        require(decoder is JsonDecoder)

        val element = decoder.decodeJsonElement()
        return TechniqueData(
            list = format.decodeFromJsonElement(ListSerializer(InjectionSerializer), element)
        )
    }

    override fun serialize(encoder: Encoder, value: TechniqueData) {
        TechniqueData.serializer().serialize(encoder, value)
    }

}