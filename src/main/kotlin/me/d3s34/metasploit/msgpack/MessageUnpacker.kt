package me.d3s34.metasploit.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.MsgPack


//fork from package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

class MessageUnpacker(private val dataBuffer: InputMessageDataPacker) {

    fun unpackNull() {
        val next = dataBuffer.requireNextByte()
        if (next != MessagePackType.NULL) throw MessagePackDeserializeException("Invalid null $next")
    }

    fun unpackBoolean(): Boolean {
        return when (val next = dataBuffer.requireNextByte()) {
            MessagePackType.Boolean.TRUE -> true
            MessagePackType.Boolean.FALSE -> false
            else -> throw MessagePackDeserializeException("Invalid boolean $next")
        }
    }

    fun unpackByte(): Byte {
        val next = dataBuffer.requireNextByte()
        return when {
            MessagePackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MessagePackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            MessagePackType.Int.isByte(next) -> dataBuffer.requireNextByte()
            else -> throw MessagePackDeserializeException("$dataBuffer: Expected byte type, but found $next")
        }
    }

    fun unpackShort(strict: Boolean = false): Short {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isShort(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).toShort()
            }
            next == MessagePackType.Int.UINT8 -> {
                dataBuffer.skip(1)
                (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
            }
            else -> if (strict)
                throw MessagePackDeserializeException("strictTypeError($dataBuffer, short, byte)")
            else unpackByte().toShort()
        }
    }

    fun unpackInt(strict: Boolean = false): Int {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isInt(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).toInt()
            }
            next == MessagePackType.Int.UINT16 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).toShort().toInt()
            }
            else -> if (strict)
                throw MessagePackDeserializeException("strictTypeError($dataBuffer, int, short")
            else unpackShort().toInt()
        }
    }

    fun unpackLong(strict: Boolean = false): Long {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isLong(next) -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(8).toLong()
            }
            next == MessagePackType.Int.UINT32 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).toInt().toLong()
            }
            else -> if (strict)
                throw MessagePackDeserializeException("strictTypeError($dataBuffer, long, int")
            else unpackInt().toLong()
        }
    }

    fun unpackFloat(): Float {
        return when (val type = dataBuffer.peek()) {
            MessagePackType.Float.FLOAT -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).toFloat()
            }
            else -> throw MessagePackDeserializeException("($dataBuffer, Expected float type, but found $type)")
        }
    }

    fun unpackDouble(strict: Boolean = false): Double {
        return when (val type = dataBuffer.peek()) {
            MessagePackType.Float.DOUBLE -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(8).toDouble()
            }
            MessagePackType.Float.FLOAT -> if (strict)
                throw MessagePackDeserializeException("strictTypeError($dataBuffer, double, float) ")
            else unpackFloat().toDouble()

            else -> throw MessagePackDeserializeException("($dataBuffer, Expected double type, but found $type)")
        }
    }

    fun unpackString(): String {
        val next = dataBuffer.requireNextByte()
        val length = when {
            MessagePackType.String.FIXSTR_SIZE_MASK.test(next) -> MessagePackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
            next == MessagePackType.String.STR8 -> dataBuffer.requireNextByte().toInt() and 0xff
            next == MessagePackType.String.STR16 -> dataBuffer.takeNext(2).toInt()
            next == MessagePackType.String.STR32 -> dataBuffer.takeNext(4).toInt()
            else -> throw MessagePackDeserializeException("($dataBuffer, Expected string type, but found $next)")

        }
        if (length == 0) return ""
        return dataBuffer.takeNext(length).decodeToString()
    }

    fun unpackByteArray(): ByteArray {
        val length = when (val next = dataBuffer.requireNextByte()) {
            MessagePackType.Bin.BIN8 -> dataBuffer.requireNextByte().toInt() and 0xff
            MessagePackType.Bin.BIN16 -> dataBuffer.takeNext(2).toInt()
            MessagePackType.Bin.BIN32 -> dataBuffer.takeNext(4).toInt()
            else -> throw MessagePackDeserializeException("($dataBuffer, Expected bytearray type, but found $next)")

        }
        if (length == 0) return byteArrayOf()
        return dataBuffer.takeNext(length)
    }

}
