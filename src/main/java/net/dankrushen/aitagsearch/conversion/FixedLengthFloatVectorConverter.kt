package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class FixedLengthFloatVectorConverter(val dimension: Int) : FloatVectorConverter() {

    override fun getLength(value: FloatVector): Int = dimension

    override fun getSize(value: FloatVector): Int = dimension * Int.SIZE_BYTES

    override fun getSizeWithCount(value: FloatVector): Int = getSize(value)

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: FloatVector, length: Int): Int {
        return writeWithoutLength(directBuffer, index, value)
    }

    override fun readCount(directBuffer: DirectBuffer, index: Int): Pair<FloatVector, Int> {
        return readWithoutLengthCount(directBuffer, index, dimension)
    }

    override fun read(directBuffer: DirectBuffer, index: Int): FloatVector {
        return readWithoutLength(directBuffer, index, dimension)
    }
}