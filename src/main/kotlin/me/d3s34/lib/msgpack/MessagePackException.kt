package me.d3s34.lib.msgpack

sealed class MessagePackException(
    override val message: String? = "",
    override val cause: Throwable? = null
): Throwable()

class MessagePackSerializeException(
    override val message: String? = "",
    override val cause: Throwable? = null
): MessagePackException()

class MessagePackDeserializeException(
    override val message: String? = "",
    override val cause: Throwable? = null
): MessagePackException()

