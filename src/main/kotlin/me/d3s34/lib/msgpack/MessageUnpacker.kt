package me.d3s34.lib.msgpack


//fork from package com.ensarsarajcic.kotlinx.serialization.msgpack.internal

class MessageUnpacker(private val dataBuffer: InputMessageDataPacker) {

    fun unpackNull() {
        val next = dataBuffer.requireNextByte()
        if (next != MessagePackType.NULL)
            throw MessagePackDeserializeException("Invalid null ${byteArrayOf(next).decodeHex()}")
    }

    fun unpackBoolean(): Boolean {
        return when (val next = dataBuffer.requireNextByte()) {
            MessagePackType.Boolean.TRUE -> true
            MessagePackType.Boolean.FALSE -> false
            else -> throw MessagePackDeserializeException("Invalid boolean $next")
        }
    }

    fun unpackInt(): ByteArray {
        val byteType = dataBuffer.requireNextByte()

        return when {
            isFixNum(byteType) -> byteArrayOf(byteType)
            isByte(byteType) -> byteArrayOf(dataBuffer.requireNextByte())
            isShort(byteType) -> dataBuffer.takeNext(2)
            isInt(byteType) -> dataBuffer.takeNext(4)
            isLong(byteType) -> dataBuffer.takeNext(8)
            else ->
                throw MessagePackDeserializeException("$dataBuffer: Expected byte type, but found ${byteType.decodeHex()}")
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
            MessagePackType.String.FIXSTR_SIZE_MASK.test(next) ->
                MessagePackType.String.FIXSTR_SIZE_MASK.unMaskValue(next).toInt()
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
            else ->
                throw MessagePackDeserializeException(
                    "(${dataBuffer.toByteArray().decodeHex()}, Expected bytearray type, but found ${next.decodeHex()})"
                )
        }
        if (length == 0) return byteArrayOf()
        return dataBuffer.takeNext(length)
    }

}
