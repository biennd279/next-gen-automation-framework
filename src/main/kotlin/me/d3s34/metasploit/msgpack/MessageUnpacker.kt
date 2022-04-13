package me.d3s34.metasploit.msgpack

import com.ensarsarajcic.kotlinx.serialization.msgpack.stream.MsgPackDataInputBuffer


//fork from package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

class MessageUnpacker(private val dataBuffer: MsgPackDataInputBuffer) {

    fun unpackNull() {
        val next = dataBuffer.requireNextByte()
        if (next != MessagePackType.NULL) throw MessagePackDeserialize("Invalid null $next")
    }

    fun unpackBoolean(): Boolean {
        return when (val next = dataBuffer.requireNextByte()) {
            MessagePackType.Boolean.TRUE -> true
            MessagePackType.Boolean.FALSE -> false
            else -> throw MessagePackDeserialize("Invalid boolean $next")
        }
    }

    fun unpackByte(strict: Boolean = false, preventOverflow: Boolean = false): Byte {
        // Check is it a single byte value
        val next = dataBuffer.requireNextByte()
        return when {
            MessagePackType.Int.POSITIVE_FIXNUM_MASK.test(next) or MessagePackType.Int.NEGATIVE_FIXNUM_MASK.test(next) -> next
            MessagePackType.Int.isByte(next) -> {
                if (next == MessagePackType.Int.UINT8 && preventOverflow) {
                    val number = (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
                    if (number !in Byte.MIN_VALUE..Byte.MAX_VALUE) {
                        throw MessagePackDeserialize("overflowError $dataBuffer")
                    } else {
                        number.toByte()
                    }
                } else {
                    dataBuffer.requireNextByte()
                }
            }
            else -> throw MessagePackDeserialize("$dataBuffer: Expected byte type, but found $next")
        }
    }

    fun unpackShort(strict: Boolean = false, preventOverflow: Boolean = false): Short {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isShort(next) -> {
                dataBuffer.skip(1)
                if (next == MessagePackType.Int.UINT16 && preventOverflow) {
                    val number = dataBuffer.takeNext(2).toInt()
                    if (number !in Short.MIN_VALUE..Short.MAX_VALUE) {
                        throw MessagePackDeserialize("overflowError $dataBuffer")
                    } else {
                        number.toShort()
                    }
                } else {
                    dataBuffer.takeNext(2).toShort()
                }
            }
            next == MessagePackType.Int.UINT8 -> {
                dataBuffer.skip(1)
                (dataBuffer.requireNextByte().toInt() and 0xff).toShort()
            }
            else -> if (strict)
                throw MessagePackDeserialize("strictTypeError($dataBuffer, short, byte)")
            else unpackByte().toShort()
        }
    }

    fun unpackInt(strict: Boolean = false, preventOverflow: Boolean = false): Int {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isInt(next) -> {
                dataBuffer.skip(1)
                if (next == MessagePackType.Int.UINT32 && preventOverflow) {
                    val number = dataBuffer.takeNext(4).toLong()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MessagePackDeserialize("overflowError($dataBuffer)")
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).toInt()
                }
            }
            next == MessagePackType.Int.UINT16 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(2).toShort().toInt()
            }
            else -> if (strict)
                throw MessagePackDeserialize("strictTypeError($dataBuffer, int, short")
            else unpackShort().toInt()
        }
    }

    fun unpackLong(strict: Boolean, preventOverflow: Boolean): Long {
        val next = dataBuffer.peek()
        return when {
            MessagePackType.Int.isLong(next) -> {
                dataBuffer.skip(1)
                if (next == MessagePackType.Int.UINT64 && preventOverflow) {
                    val number = dataBuffer.takeNext(8).toLong()
                    if (number < 0) {
                        throw MessagePackDeserialize("overflowError($dataBuffer)")
                    } else {
                        number
                    }
                } else {
                    dataBuffer.takeNext(8).toLong()
                }
            }
            next == MessagePackType.Int.UINT32 -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).toInt().toLong()
            }
            else -> if (strict)
                throw MessagePackDeserialize("strictTypeError($dataBuffer, long, int")
            else unpackInt().toLong()
        }
    }

    fun unpackFloat(strict: Boolean = false): Float {
        return when (val type = dataBuffer.peek()) {
            MessagePackType.Float.FLOAT -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(4).toFloat()
            }
            else -> throw MessagePackDeserialize("($dataBuffer, Expected float type, but found $type)")
        }
    }

    fun unpackDouble(strict: Boolean = false): Double {
        return when (val type = dataBuffer.peek()) {
            MessagePackType.Float.DOUBLE -> {
                dataBuffer.skip(1)
                dataBuffer.takeNext(8).toDouble()
            }
            MessagePackType.Float.FLOAT -> if (strict)
                throw MessagePackDeserialize("strictTypeError($dataBuffer, double, float) ")
            else unpackFloat().toDouble()

            else -> throw MessagePackDeserialize("($dataBuffer, Expected double type, but found $type)")
        }
    }

    fun unpackString(preventOverflow: Boolean = false): String {
        val next = dataBuffer.requireNextByte()
        val length = when {
            MessagePackType.String.FIXSTR_SIZE_MASK.test(next) -> MessagePackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
            next == MessagePackType.String.STR8 -> dataBuffer.requireNextByte().toInt() and 0xff
            next == MessagePackType.String.STR16 -> dataBuffer.takeNext(2).toInt()
            next == MessagePackType.String.STR32 -> {
                if (preventOverflow) {
                    val number = dataBuffer.takeNext(4).toLong()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MessagePackDeserialize("overflowError($dataBuffer)")
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).toInt()
                }
            }
            else -> throw MessagePackDeserialize("($dataBuffer, Expected string type, but found $next)")

        }
        if (length == 0) return ""
        return dataBuffer.takeNext(length).decodeToString()
    }

    fun unpackByteArray(preventOverflow: Boolean = false): ByteArray {
        val next = dataBuffer.requireNextByte()
        val length = when (next) {
            MessagePackType.Bin.BIN8 -> dataBuffer.requireNextByte().toInt() and 0xff
            MessagePackType.Bin.BIN16 -> dataBuffer.takeNext(2).toInt()
            MessagePackType.Bin.BIN32 -> {
                if (preventOverflow) {
                    val number = dataBuffer.takeNext(4).toLong()
                    if (number !in Int.MIN_VALUE..Int.MAX_VALUE) {
                        throw MessagePackDeserialize("overflowError($dataBuffer)")
                    } else {
                        number.toInt()
                    }
                } else {
                    dataBuffer.takeNext(4).toInt()
                }
            }
            else -> throw MessagePackDeserialize("($dataBuffer, Expected bytearray type, but found $next)")

        }
        if (length == 0) return byteArrayOf()
        return dataBuffer.takeNext(length)
    }

}
