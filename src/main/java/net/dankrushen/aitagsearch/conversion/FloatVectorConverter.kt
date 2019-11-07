package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.datatypes.FloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVectorWithoutLengthCount
import net.dankrushen.aitagsearch.extensions.putFloatVectorWithoutLength
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class FloatVectorConverter : DirectBufferConverter<FloatVector>() {
    companion object {
        val converter: FloatVectorConverter = FloatVectorConverter()
    }

    override fun getLength(value: FloatVector): Int {
        return value.dimension
    }

    override fun getSize(value: FloatVector): Int {
        return value.sizeBytes
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: FloatVector): Int {
        return directBuffer.putFloatVectorWithoutLength(index, value)
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<FloatVector, Int> {
        return directBuffer.getFloatVectorWithoutLengthCount(index, length)
    }
}