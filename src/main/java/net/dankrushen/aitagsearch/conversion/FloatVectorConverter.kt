package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

open class FloatVectorConverter : DirectBufferConverter<FloatVector>() {
    companion object {
        val converter = FloatVectorConverter()
    }

    override fun getLength(value: FloatVector): Int = value.dimension

    override fun getSize(value: FloatVector): Int = value.sizeBytes

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: FloatVector): Int {
        var bytesWritten = 0

        val floatArray = value.dims

        for (dimensionValue in floatArray) {
            val byteIndex = index + bytesWritten

            directBuffer.putFloat(byteIndex, dimensionValue)
            bytesWritten += Int.SIZE_BYTES
        }

        return bytesWritten
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<FloatVector, Int> {
        var bytesRead = 0

        val floatArray = FloatArray(length)

        for (i in 0 until length) {
            val dimIndex = index + bytesRead

            floatArray[i] = directBuffer.getFloat(dimIndex)
            bytesRead += Int.SIZE_BYTES
        }

        return Pair(FloatVector(floatArray), bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): FloatVector {
        val floatArray = FloatArray(length)

        for (i in 0 until length) {
            val dimIndex = index + (i * Int.SIZE_BYTES)

            floatArray[i] = directBuffer.getFloat(dimIndex)
        }

        return FloatVector(floatArray)
    }
}