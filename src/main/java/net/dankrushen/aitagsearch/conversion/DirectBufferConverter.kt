package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer
import org.agrona.concurrent.UnsafeBuffer
import java.nio.ByteBuffer

abstract class DirectBufferConverter<T> {

    abstract fun getLength(value: T): Int

    open fun getSize(value: T): Int = getLength(value)

    open fun getLengthWithCount(value: T): Int = getLength(value) + Int.SIZE_BYTES

    // region Write
    abstract fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: T): Int

    open fun write(directBuffer: MutableDirectBuffer, index: Int, value: T, length: Int): Int {
        var bytesWritten = 0

        directBuffer.putInt(index, length)
        bytesWritten += Int.SIZE_BYTES

        bytesWritten += writeWithoutLength(directBuffer, index + bytesWritten, value)

        return bytesWritten
    }

    open fun write(directBuffer: MutableDirectBuffer, index: Int, value: T): Int {
        return write(directBuffer, index, value, getLength(value))
    }
    // endregion

    open fun toDirectBuffer(value: T, size: Int, length: Int): DirectBuffer {
        val buffer = UnsafeBuffer(ByteBuffer.allocateDirect(size + Int.SIZE_BYTES))

        write(buffer, 0, value, length)

        return buffer
    }

    open fun toDirectBuffer(value: T): DirectBuffer {
        return toDirectBuffer(value, getSize(value), getLength(value))
    }

    open fun toDirectBufferWithoutLength(value: T, size: Int): DirectBuffer {
        val buffer = UnsafeBuffer(ByteBuffer.allocateDirect(size))

        writeWithoutLength(buffer, 0, value)

        return buffer
    }

    open fun toDirectBufferWithoutLength(value: T): DirectBuffer {
        return toDirectBufferWithoutLength(value, getSize(value))
    }

    // region Read
    abstract fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<T, Int>

    open fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): T = readWithoutLengthCount(directBuffer, index, length).first

    open fun readCount(directBuffer: DirectBuffer, index: Int): Pair<T, Int> {
        var bytesRead = 0

        val length = directBuffer.getInt(index)
        bytesRead += Int.SIZE_BYTES

        val value = readWithoutLengthCount(directBuffer, index + bytesRead, length)
        bytesRead += value.second

        return Pair(value.first, bytesRead)
    }

    open fun read(directBuffer: DirectBuffer, index: Int): T = readCount(directBuffer, index).first
    // endregion
}