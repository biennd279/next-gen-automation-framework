package me.d3s34.metasploit.msgpack

import java.nio.ByteBuffer


fun Short.toByteArray() = ByteArray(2) { ((this.toInt() shr (1-it) * 8) and 0xFF).toByte() }
fun Int.toByteArray() = ByteArray(4) { ((this shr (3-it) * 8) and 0xFF).toByte() }
fun Long.toByteArray() = ByteArray(8) { ((this shr (7-it) * 8) and 0xFF).toByte() }

//TODO: remake to get performance
fun ByteArray.pad(size: Int): ByteArray {
    if (this.size == size)
        return this

    return ByteArray(size - this.size) { 0 } + this
}

fun ByteArray.toByte(): Byte {
    return first()
}

fun ByteArray.toShort(): Short {
    return ByteBuffer.wrap(pad(2)).short
}

fun ByteArray.toInt(): Int {
    return ByteBuffer.wrap(pad(4)).int
}

fun ByteArray.toLong(): Long {
    return ByteBuffer.wrap(pad(8)).long
}

fun ByteArray.toFloat(): Float {
    require(size == 4)
    return ByteBuffer.wrap(this).float
}

fun ByteArray.toDouble(): Double {
    require(size == 8)
    return ByteBuffer.wrap(this).double
}

fun ByteArray.decodeHex() = this.joinToString(separator = "") { it.decodeHex() }
fun Byte.decodeHex() = toInt().and(0xff).toString(16).padStart(2, '0')

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0)

    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun isPrimitive(value: Any): Boolean = when (value) {
    is Boolean,
    is Byte,
    is Short,
    is Int,
    is Long,
    is Float,
    is Double,
    is String,
    is Char -> true
    else -> false
}
fun isBoolean(byte: Byte): Boolean = MessagePackType.Boolean.isBoolean(byte)
fun isInt(byte: Byte): Boolean = MessagePackType.Int.isInt(byte)
fun isByte(byte: Byte): Boolean = MessagePackType.Int.isByte(byte)
fun isShort(byte: Byte): Boolean = MessagePackType.Int.isShort(byte)
fun isLong(byte: Byte): Boolean = MessagePackType.Int.isLong(byte)
fun isFloat(byte: Byte): Boolean = MessagePackType.Float.isFloat(byte)
fun isDouble(byte: Byte): Boolean = MessagePackType.Float.isDouble(byte)
fun isString(byte: Byte): Boolean = MessagePackType.String.isString(byte)
fun isBinary(byte: Byte): Boolean = MessagePackType.Bin.isBinary(byte)
fun isFixNum(byte: Byte): Boolean = MessagePackType.Int.isFixNum(byte)
fun isIntNumber(byte: Byte): Boolean = MessagePackType.Int.isIntNumber(byte)
fun isMap(byte: Byte): Boolean = MessagePackType.Map.isMap(byte)
fun isArray(byte: Byte): Boolean = MessagePackType.Array.isArray(byte)
