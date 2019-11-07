package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

fun MutableDirectBuffer.putFloatVectorWithoutLength(index: Int, value: FloatVector): Int {
    var bytesWritten = 0

    for (i in 0 until value.dimension) {
        val dimIndex = index + bytesWritten

        this.putFloat(dimIndex, value[i])
        bytesWritten += Int.SIZE_BYTES
    }

    return bytesWritten
}

fun MutableDirectBuffer.putFloatVector(index: Int, value: FloatVector): Int {
    var bytesWritten = 0

    this.putInt(index, value.dimension)
    bytesWritten += Int.SIZE_BYTES

    bytesWritten += this.putFloatVectorWithoutLength(index + bytesWritten, value)

    return bytesWritten
}

fun DirectBuffer.getFloatVectorWithoutLengthCount(index: Int, length: Int): Pair<FloatVector, Int> {
    var bytesRead = 0

    val floatVector = FloatVector(length)

    for (i in 0 until length) {
        val dimIndex = index + bytesRead

        floatVector[i] = this.getFloat(dimIndex)
        bytesRead += Int.SIZE_BYTES
    }

    return Pair(floatVector, bytesRead)
}

fun DirectBuffer.getFloatVectorWithoutLength(index: Int, length: Int): FloatVector = getFloatVectorWithoutLengthCount(index, length).first

fun DirectBuffer.getFloatVectorCount(index: Int): Pair<FloatVector, Int> {
    var bytesRead = 0

    val dimension = this.getInt(index)
    bytesRead += Int.SIZE_BYTES

    val value = getFloatVectorWithoutLengthCount(index + bytesRead, dimension)
    bytesRead += value.second

    return Pair(value.first, bytesRead)
}

fun DirectBuffer.getFloatVector(index: Int): FloatVector = getFloatVectorCount(index).first