package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.conversion.FloatVectorConverter
import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

fun MutableDirectBuffer.putFloatVectorWithoutLength(index: Int, value: FloatVector): Int {
    return FloatVectorConverter.converter.writeWithoutLength(this, index, value)
}

fun MutableDirectBuffer.putFloatVector(index: Int, value: FloatVector): Int {
    return FloatVectorConverter.converter.write(this, index, value)
}

fun DirectBuffer.getFloatVectorWithoutLengthCount(index: Int, length: Int): Pair<FloatVector, Int> {
    return FloatVectorConverter.converter.readWithoutLengthCount(this, index, length)
}

fun DirectBuffer.getFloatVectorWithoutLength(index: Int, length: Int): FloatVector {
    return FloatVectorConverter.converter.readWithoutLength(this, index, length)
}

fun DirectBuffer.getFloatVectorCount(index: Int): Pair<FloatVector, Int> {
    return FloatVectorConverter.converter.readCount(this, index)
}

fun DirectBuffer.getFloatVector(index: Int): FloatVector {
    return FloatVectorConverter.converter.read(this, index)
}