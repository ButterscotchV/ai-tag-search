package net.dankrushen.aitagsearch.conversion.basetypes

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class StringConverter : DirectBufferConverter<String>() {
    companion object {
        val converter = StringConverter()
    }

    internal fun getStringBytes(value: String): ByteArray = value.toByteArray(Charsets.UTF_8)

    internal fun bytesToString(bytes: ByteArray): String = bytes.toString(Charsets.UTF_8)

    override fun getLength(value: String): Int = value.length

    override fun getSize(value: String): Int = getStringBytes(value).size

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: String): Int {
        return ByteArrayConverter.converter.writeWithoutLength(directBuffer, index, getStringBytes(value))
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: String, length: Int): Int {
        return ByteArrayConverter.converter.write(directBuffer, index, getStringBytes(value), length)
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: String): Int {
        return ByteArrayConverter.converter.write(directBuffer, index, getStringBytes(value))
    }

    override fun toDirectBuffer(index: Int, value: String, sizeBytes: Int, length: Int): MutableDirectBuffer {
        return ByteArrayConverter.converter.toDirectBuffer(index, getStringBytes(value), sizeBytes, length)
    }

    override fun toDirectBuffer(index: Int, value: String): MutableDirectBuffer {
        return ByteArrayConverter.converter.toDirectBuffer(index, getStringBytes(value))
    }

    override fun toDirectBufferWithoutLength(index: Int, value: String, sizeBytes: Int): MutableDirectBuffer {
        return ByteArrayConverter.converter.toDirectBufferWithoutLength(index, getStringBytes(value), sizeBytes)
    }

    override fun toDirectBufferWithoutLength(index: Int, value: String): MutableDirectBuffer {
        return ByteArrayConverter.converter.toDirectBufferWithoutLength(index, getStringBytes(value))
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<String, Int> {
        val byteValue = ByteArrayConverter.converter.readWithoutLengthCount(directBuffer, index, length)

        return Pair(bytesToString(byteValue.first), byteValue.second)
    }
}