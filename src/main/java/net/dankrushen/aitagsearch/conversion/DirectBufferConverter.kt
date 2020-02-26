package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer
import org.agrona.concurrent.UnsafeBuffer
import java.nio.ByteBuffer

abstract class DirectBufferConverter<T> {

    abstract fun getLength(value: T): Int

    open fun getSize(value: T): Int = getLength(value)

    open fun getSizeWithCount(value: T): Int = getSize(value) + Int.SIZE_BYTES

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

    open fun toDirectBuffer(index: Int, value: T, sizeBytes: Int, length: Int): MutableDirectBuffer {
        val buffer = UnsafeBuffer(ByteBuffer.allocateDirect(index + sizeBytes))

        write(buffer, index, value, length)

        return buffer
    }

    open fun toDirectBuffer(index: Int, value: T): MutableDirectBuffer {
        return toDirectBuffer(index, value, getSizeWithCount(value), getLength(value))
    }

    open fun toDirectBufferWithoutLength(index: Int, value: T, sizeBytes: Int): MutableDirectBuffer {
        val buffer = UnsafeBuffer(ByteBuffer.allocateDirect(index + sizeBytes))

        writeWithoutLength(buffer, index, value)

        return buffer
    }

    open fun toDirectBufferWithoutLength(index: Int, value: T): MutableDirectBuffer {
        return toDirectBufferWithoutLength(index, value, getSize(value))
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

    open fun read(directBuffer: DirectBuffer, index: Int): T {
        val length = directBuffer.getInt(index)
        return readWithoutLength(directBuffer, index + Int.SIZE_BYTES, length)
    }
    // endregion
}