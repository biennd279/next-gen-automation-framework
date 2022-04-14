package me.d3s34.metasploit.rpcapi

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import me.d3s34.metasploit.msgpack.MessagePack
import me.d3s34.metasploit.msgpack.MessagePackException

class MessagePackConverter(
    private val messagePack: MessagePack = MessagePack()
): ContentConverter {

    private val module = messagePack.serializersModule

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    override suspend fun deserialize(charset: Charset, typeInfo: TypeInfo, content: ByteReadChannel): Any? {

        val serializer = typeInfo.kotlinType
            ?.let { type ->
                if (type.arguments.isEmpty()) null // fallback to simple case because of
                // https://github.com/Kotlin/kotlinx.serialization/issues/1870
                else module.serializerOrNull(type)
            }
            ?: module.getContextual(typeInfo.type)
            ?: typeInfo.type.serializer()

        @Suppress("UNCHECKED_CAST")
        return kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val byteArray = content.toByteArray()
                messagePack.decodeFromByteArray(serializer, byteArray)
            }
        }
            .onFailure {
                if (it is MessagePackException) {

                }
            }
            .getOrNull()
    }

    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any
    ): OutgoingContent {
        return object: OutgoingContent.ByteArrayContent() {
            override val contentType = MessagePackContentType

            override fun bytes(): ByteArray = messagePack.encodeToByteArray(value)
        }
    }

}

fun Configuration.messagePack(
    converter: MessagePackConverter = MessagePackConverter(),
    block: Configuration.() -> Unit
) {
    register(MessagePackContentType, converter)
    this.apply(block)
}
