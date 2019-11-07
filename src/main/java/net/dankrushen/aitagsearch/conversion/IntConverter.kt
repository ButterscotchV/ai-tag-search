package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class IntConverter : DirectBufferConverter<Int>() {
    companion object {
        val converter: IntConverter = IntConverter()
    }

    override fun getLength(value: Int): Int = Int.SIZE_BYTES

    override fun getLengthWithCount(value: Int): Int = Int.SIZE_BYTES

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: Int): Int {
        directBuffer.putInt(index, value)
        return Int.SIZE_BYTES
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: Int, length: Int): Int {
        return writeWithoutLength(directBuffer, index, value)
    }

    override fun write(directBuffer: MutableDirectBuffer, index: Int, value: Int): Int {
        return writeWithoutLength(directBuffer, index, value)
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<Int, Int> {
        return Pair(directBuffer.getInt(index), Int.SIZE_BYTES)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): Int {
        return directBuffer.getInt(index)
    }

    override fun readCount(directBuffer: DirectBuffer, index: Int): Pair<Int, Int> {
        return Pair(directBuffer.getInt(index), Int.SIZE_BYTES)
    }

    override fun read(directBuffer: DirectBuffer, index: Int): Int {
        return directBuffer.getInt(index)
    }
}