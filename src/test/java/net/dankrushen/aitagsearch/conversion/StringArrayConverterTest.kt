package net.dankrushen.aitagsearch.conversion

import org.agrona.concurrent.UnsafeBuffer
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import kotlin.test.assertEquals

internal class StringArrayConverterTest {

    val demoArray = arrayOf("Test1", "Test2", "Test3", "Something Else", "Whatever")

    val expectedSize = (Int.SIZE_BYTES * demoArray.size) + (5 * 3) + 14 + 8
    val demoDirectBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(expectedSize))

    val converter = ArrayConverter.stringArrayConverter

    @Test
    fun readAndWriteWithoutLength() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = converter.readWithoutLengthCount(demoDirectBuffer, 0, demoArray.size)

        assertEquals(expectedSize, readArray.second)
        assert(demoArray.contentEquals(readArray.first))
    }

    @Test
    fun writeWithoutLength() {
        val bytesWritten = converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)

        assertEquals(expectedSize, bytesWritten)
    }

    @Test
    fun readWithoutLengthCount() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = converter.readWithoutLengthCount(demoDirectBuffer, 0, demoArray.size)

        assertEquals(expectedSize, readArray.second)
        assert(demoArray.contentEquals(readArray.first))
    }

    @Test
    fun readWithoutLength() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = converter.readWithoutLength(demoDirectBuffer, 0, demoArray.size)

        assert(demoArray.contentEquals(readArray))
    }
}