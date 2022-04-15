package me.d3s34.lib.msgpack


//fork from package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

class MessagePacker {
    fun packNull(): ByteArray {
        return byteArrayOf(MessagePackType.NULL)
    }

    fun packBoolean(value: Boolean): ByteArray {
        return byteArrayOf(MessagePackType.Boolean(value))
    }

    fun packByte(value: Byte, strict: Boolean = false): ByteArray {
        return if (value >= MessagePackType.Int.MIN_NEGATIVE_SINGLE_BYTE) {
            byteArrayOf(value)
        } else {
            byteArrayOf(MessagePackType.Int.INT8, value)
        }
    }

    fun packShort(value: Short, strict: Boolean = false): ByteArray {
        return if (value in MessagePackType.Int.MIN_NEGATIVE_BYTE..Byte.MAX_VALUE && !strict) {
            packByte(value.toByte())
        } else {
            var uByte = false
            val type =
                when {
                    value < 0 -> MessagePackType.Int.INT16
                    value <= MessagePackType.Int.MAX_UBYTE && !strict -> MessagePackType.Int.UINT8.also { uByte = true }
                    else -> MessagePackType.Int.UINT16
                }
            if (uByte) {
                byteArrayOf(type, (value.toInt() and 0xff).toByte())
            } else {
                byteArrayOf(type) + value.toByteArray()
            }
        }
    }

    fun packInt(value: Int, strict: Boolean = false): ByteArray {
        return if (value in Short.MIN_VALUE..Short.MAX_VALUE && !strict) {
            packShort(value.toShort())
        } else {
            var uShort = false
            val type =
                when {
                    value < 0 -> MessagePackType.Int.INT32
                    value <= MessagePackType.Int.MAX_USHORT && !strict -> MessagePackType.Int.UINT16.also {
                        uShort = true
                    }
                    else -> MessagePackType.Int.UINT32
                }
            if (uShort) {
                byteArrayOf(type) + value.toShort().toByteArray()
            } else {
                byteArrayOf(type) + value.toByteArray()
            }
        }
    }

    fun packLong(value: Long, strict: Boolean = false): ByteArray {
        return if (value in Int.MIN_VALUE..Int.MAX_VALUE && !strict) {
            packInt(value.toInt())
        } else {
            var uInt = false
            val type =
                when {
                    value < 0 -> MessagePackType.Int.INT64
                    value <= MessagePackType.Int.MAX_UINT && !strict -> MessagePackType.Int.UINT32.also { uInt = true }
                    else -> MessagePackType.Int.UINT64
                }
            if (uInt) {
                byteArrayOf(type) + value.toInt().toByteArray()
            } else {
                byteArrayOf(type) + value.toByteArray()
            }
        }
    }

    fun packFloat(value: Float): ByteArray {
        return byteArrayOf(MessagePackType.Float.FLOAT) + value.toRawBits().toByteArray()
    }

    fun packDouble(value: Double): ByteArray {
        return byteArrayOf(MessagePackType.Float.DOUBLE) + value.toRawBits().toByteArray()
    }

    fun packString(value: String, rawCompatibility: Boolean = false): ByteArray {
        val bytes = value.encodeToByteArray()
        val prefix = when {
            bytes.size <= MessagePackType.String.MAX_FIXSTR_LENGTH -> {
                byteArrayOf(MessagePackType.String.FIXSTR_SIZE_MASK.maskValue(bytes.size.toByte()))
            }
            bytes.size <= MessagePackType.String.MAX_STR8_LENGTH && !rawCompatibility -> {
                byteArrayOf(MessagePackType.String.STR8) + bytes.size.toByte()
            }
            bytes.size <= MessagePackType.String.MAX_STR16_LENGTH -> {
                byteArrayOf(MessagePackType.String.STR16) + bytes.size.toShort().toByteArray()
            }
            bytes.size <= MessagePackType.String.MAX_STR32_LENGTH -> {
                byteArrayOf(MessagePackType.String.STR32) + bytes.size.toByteArray()
            }
            else -> throw MessagePackSerializeException()
        }
        return prefix + bytes
    }

    fun packByteArray(value: ByteArray): ByteArray {
        val prefix = when {
            value.size <= MessagePackType.Bin.MAX_BIN8_LENGTH -> {
                byteArrayOf(MessagePackType.Bin.BIN8) + value.size.toByte()
            }
            value.size <= MessagePackType.Bin.MAX_BIN16_LENGTH -> {
                byteArrayOf(MessagePackType.Bin.BIN16) + value.size.toShort().toByteArray()
            }
            value.size <= MessagePackType.Bin.MAX_BIN32_LENGTH -> {
                byteArrayOf(MessagePackType.Bin.BIN32) + value.size.toByteArray()
            }
            else -> throw MessagePackSerializeException()
        }
        return prefix + value
    }
}
