package me.d3s34.metasploit.rpcapi

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.serializer


class MessagePack(
    val msgPack: MsgPack
) {

    companion object Plugin : HttpClientPlugin<Unit, MessagePack> {

        override val key: AttributeKey<MessagePack>
            get() = AttributeKey("ClientMessagePack")

        override fun prepare(block: Unit.() -> Unit): MessagePack {
            return MessagePack(MsgPack.Default)
        }

        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        override fun install(plugin: MessagePack, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { payload ->
//                val contentType = context.contentType() ?: return@intercept
//
//                if (contentType != MessPackContentType.MessagePack) return@intercept

                val serializedContent = when (payload) {
                    Unit -> EmptyContent
                    is EmptyContent -> EmptyContent
                    else -> object : OutgoingContent.ByteArrayContent() {
                        override fun bytes() = plugin.msgPack.encodeToByteArray(payload)
                    }
                }

                proceedWith(serializedContent)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                if (body !is ByteReadChannel) return@intercept

                val contentType = context.response.contentType() ?: return@intercept

                if (contentType != MessPackContentType.MessagePack) return@intercept

                val byteArray = body.toByteArray()

                val deserializationStrategy = plugin.msgPack.serializersModule.getContextual(info.type)
                val mapper =
                    deserializationStrategy ?: (info.kotlinType?.let { serializer(it) } ?: info.type.serializer())

                val parsedBody = plugin.msgPack.decodeFromByteArray(mapper, byteArray)!!
                val response = HttpResponseContainer(info, parsedBody)

                proceedWith(response)
            }
        }
    }
}

