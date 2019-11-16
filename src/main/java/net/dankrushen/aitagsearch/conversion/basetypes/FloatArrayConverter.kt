package net.dankrushen.aitagsearch.conversion.basetypes

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class FloatArrayConverter : DirectBufferConverter<FloatArray>() {
    companion object {
        val converter = FloatArrayConverter()
    }

    override fun getLength(value: FloatArray): Int {
        return value.size
    }

    override fun getSize(value: FloatArray): Int {
        return value.size * Int.SIZE_BYTES
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: FloatArray): Int {
        var bytesWritten = 0

        for (entry in value) {
            val byteIndex = index + bytesWritten

            directBuffer.putFloat(byteIndex, entry)
            bytesWritten += Int.SIZE_BYTES
        }

        return bytesWritten
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<FloatArray, Int> {
        var bytesRead = 0

        val array = FloatArray(length)

        for (i in 0 until length) {
            val dimIndex = index + bytesRead

            array[i] = directBuffer.getFloat(dimIndex)
            bytesRead += Int.SIZE_BYTES
        }

        return Pair(array, bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): FloatArray {
        val array = FloatArray(length)

        for (i in 0 until length) {
            val dimIndex = index + (i * Int.SIZE_BYTES)

            array[i] = directBuffer.getFloat(dimIndex)
        }

        return array
    }
}