package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer
import org.agrona.concurrent.UnsafeBuffer

class ByteArrayConverter : DirectBufferConverter<ByteArray>() {
    companion object {
        val converter: ByteArrayConverter = ByteArrayConverter()
    }

    override fun getLength(value: ByteArray): Int = value.size

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: ByteArray): Int {
        directBuffer.putBytes(index, value)
        return getLength(value)
    }

    override fun toDirectBufferWithoutLength(value: ByteArray, length: Int): DirectBuffer {
        return UnsafeBuffer(value)
    }

    override fun toDirectBufferWithoutLength(value: ByteArray): DirectBuffer {
        return UnsafeBuffer(value)
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<ByteArray, Int> {
        val byteArray = ByteArray(length)

        directBuffer.getBytes(index, byteArray)

        return Pair(byteArray, getLength(byteArray))
    }
}