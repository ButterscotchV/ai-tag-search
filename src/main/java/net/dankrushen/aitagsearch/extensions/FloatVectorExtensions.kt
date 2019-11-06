package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.types.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

fun DirectBuffer.toFloatVector(): FloatVector = FloatVector.fromDirectBuffer(this)

fun MutableDirectBuffer.putFloatVector(index: Int, value: FloatVector): Int {
    this.putInt(index, value.dimension)

    for (i in 0 until value.dimension) {
        // index + ((dimension count int + dimension value index) * integer bytes)
        val dimIndex = index + ((1 + i) * Int.SIZE_BYTES)
        this.putFloat(dimIndex, value[i])
    }

    return value.sizeBytes
}

fun DirectBuffer.getFloatVector(index: Int): FloatVector {
    val dimension = this.getInt(index)
    val floatVector = FloatVector(dimension)

    for (i in 0 until dimension) {
        // index + ((dimension count int + dimension value index) * integer bytes)
        val dimIndex = index + ((1 + i) * Int.SIZE_BYTES)
        floatVector[i] = this.getFloat(dimIndex)
    }

    return floatVector
}