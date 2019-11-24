package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class FixedLengthFloatVectorConverter(val dimension: Int) : FloatVectorConverter() {

    override fun getLength(value: FloatVector): Int = dimension

    override fun getSize(value: FloatVector): Int = dimension * Int.SIZE_BYTES

    override fun getSizeWithCount(value: FloatVector): Int = getSize(value)

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: FloatVector, length: Int): Int {
        var bytesWritten = 0

        bytesWritten += writeWithoutLength(directBuffer, index, value)

        return bytesWritten
    }

    override fun readCount(directBuffer: DirectBuffer, index: Int): Pair<FloatVector, Int> {
        var bytesRead = 0

        val value = readWithoutLengthCount(directBuffer, index, dimension)
        bytesRead += value.second

        return Pair(value.first, bytesRead)
    }

    override fun read(directBuffer: DirectBuffer, index: Int): FloatVector {
        return readWithoutLength(directBuffer, index, dimension)
    }
}