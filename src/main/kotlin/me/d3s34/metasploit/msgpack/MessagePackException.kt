package me.d3s34.metasploit.msgpack

sealed class MessagePackException(
    override val message: String? = "",
    override val cause: Throwable? = null
): Throwable()

class MessagePackSerialize(
    override val message: String? = "",
    override val cause: Throwable? = null
): MessagePackException()

class MessagePackDeserialize(
    override val message: String? = "",
    override val cause: Throwable? = null
): MessagePackException()

