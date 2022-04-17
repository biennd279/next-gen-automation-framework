package me.d3s34.metasploit.rpcapi

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import me.d3s34.metasploit.rpcapi.request.MsfRpcRequest

class MsfRpc {
    companion object Plugin: HttpClientPlugin<MsfRpc, MsfRpc> {
        override val key: AttributeKey<MsfRpc>
            get() = AttributeKey("MessagePack")

        override fun prepare(block: MsfRpc.() -> Unit): MsfRpc {
            return MsfRpc().apply(block)
        }

        override fun install(plugin: MsfRpc, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) {
                //Only process msfrpc request
                val contentType = context.contentType() ?: return@intercept
                if (contentType != MessagePackContentType) return@intercept
                if (it !is MsfRpcRequest) return@intercept

                val msfRequest = it.toMsfRequest()
                context.setBody(msfRequest)
                subject = msfRequest
            }
        }

    }
}
