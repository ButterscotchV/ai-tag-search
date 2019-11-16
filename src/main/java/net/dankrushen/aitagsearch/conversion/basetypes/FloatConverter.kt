package net.dankrushen.aitagsearch.conversion.basetypes

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class FloatConverter : DirectBufferConverter<Float>() {
    companion object {
        val converter = FloatConverter()
    }

    override fun getLength(value: Float): Int = 1

    override fun getSize(value: Float): Int = Int.SIZE_BYTES

    override fun getSizeWithCount(value: Float): Int = Int.SIZE_BYTES

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: Float): Int {
        directBuffer.putFloat(index, value)
        return Int.SIZE_BYTES
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: Float, length: Int): Int {
        return writeWithoutLength(directBuffer, index, value)
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: Float): Int {
        return writeWithoutLength(directBuffer, index, value)
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<Float, Int> {
        return Pair(directBuffer.getFloat(index), Int.SIZE_BYTES)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): Float {
        return directBuffer.getFloat(index)
    }

    override fun readCount(directBuffer: DirectBuffer, index: Int): Pair<Float, Int> {
        return Pair(directBuffer.getFloat(index), Int.SIZE_BYTES)
    }

    override fun read(directBuffer: DirectBuffer, index: Int): Float {
        return directBuffer.getFloat(index)
    }
}