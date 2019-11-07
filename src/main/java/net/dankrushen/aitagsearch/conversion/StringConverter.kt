package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class StringConverter : DirectBufferConverter<String>() {
    companion object {
        val converter: StringConverter = StringConverter()
    }

    private fun getStringBytes(value: String): ByteArray {
        return value.toByteArray(Charsets.UTF_8)
    }

    private fun bytesToString(bytes: ByteArray): String {
        return bytes.toString(Charsets.UTF_8)
    }

    override fun getLength(value: String): Int {
        return getStringBytes(value).size
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: String): Int {
        return ByteArrayConverter.converter.writeWithoutLength(directBuffer, index, getStringBytes(value))
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: String, length: Int): Int {
        return ByteArrayConverter.converter.write(directBuffer, index, getStringBytes(value), length)
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: String): Int {
        return ByteArrayConverter.converter.write(directBuffer, index, getStringBytes(value))
    }

    override fun toDirectBuffer(value: String, length: Int): DirectBuffer {
        return ByteArrayConverter.converter.toDirectBuffer(getStringBytes(value), length)
    }

    override fun toDirectBuffer(value: String): DirectBuffer {
        return ByteArrayConverter.converter.toDirectBuffer(getStringBytes(value))
    }

    override fun toDirectBufferWithoutLength(value: String, length: Int): DirectBuffer {
        return ByteArrayConverter.converter.toDirectBufferWithoutLength(getStringBytes(value), length)
    }

    override fun toDirectBufferWithoutLength(value: String): DirectBuffer {
        return ByteArrayConverter.converter.toDirectBufferWithoutLength(getStringBytes(value))
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<String, Int> {
        val byteValue = ByteArrayConverter.converter.readWithoutLengthCount(directBuffer, index, length)

        return Pair(bytesToString(byteValue.first), byteValue.second)
    }
}