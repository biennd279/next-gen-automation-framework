package me.d3s34.metasploit.rpcapi

import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import me.d3s34.metasploit.msgpack.MessagePack

fun Configuration.messagePack() {
    serialization(MessagePackContentType, MessagePack())
}
