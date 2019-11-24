package net.dankrushen.aitagsearch.conversion.basetypes

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class IntArrayConverter : DirectBufferConverter<IntArray>() {
    companion object {
        val converter = IntArrayConverter()
    }

    override fun getLength(value: IntArray): Int = value.size

    override fun getSize(value: IntArray): Int = value.size * Int.SIZE_BYTES

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: IntArray): Int {
        var bytesWritten = 0

        for (entry in value) {
            val byteIndex = index + bytesWritten

            directBuffer.putInt(byteIndex, entry)
            bytesWritten += Int.SIZE_BYTES
        }

        return bytesWritten
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<IntArray, Int> {
        var bytesRead = 0

        val array = IntArray(length)

        for (i in 0 until length) {
            val dimIndex = index + bytesRead

            array[i] = directBuffer.getInt(dimIndex)
            bytesRead += Int.SIZE_BYTES
        }

        return Pair(array, bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): IntArray {
        val array = IntArray(length)

        for (i in 0 until length) {
            val dimIndex = index + (i * Int.SIZE_BYTES)

            array[i] = directBuffer.getInt(dimIndex)
        }

        return array
    }
}