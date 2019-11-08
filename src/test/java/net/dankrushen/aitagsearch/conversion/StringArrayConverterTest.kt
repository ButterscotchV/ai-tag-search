package net.dankrushen.aitagsearch.conversion

import org.agrona.concurrent.UnsafeBuffer
import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import kotlin.test.assertEquals

internal class StringArrayConverterTest {

    val demoArray = arrayOf("Test1", "Test2", "Test3", "Something Else", "Whatever")

    val expectedSize = (Int.SIZE_BYTES * demoArray.size) + (5 * 3) + 14 + 8
    val demoDirectBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(expectedSize))

    fun readAndWriteWithoutLength() {
        StringArrayConverter.converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = StringArrayConverter.converter.readWithoutLengthCount(demoDirectBuffer, 0, demoArray.size)

        assertEquals(expectedSize, readArray.second)
        assert(demoArray.contentEquals(readArray.first))
    }

    @Test
    fun writeWithoutLength() {
        val bytesWritten = StringArrayConverter.converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)

        assertEquals(expectedSize, bytesWritten)
    }

    @Test
    fun readWithoutLengthCount() {
        StringArrayConverter.converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = StringArrayConverter.converter.readWithoutLengthCount(demoDirectBuffer, 0, demoArray.size)

        assertEquals(expectedSize, readArray.second)
        assert(demoArray.contentEquals(readArray.first))
    }

    @Test
    fun readWithoutLength() {
        StringArrayConverter.converter.writeWithoutLength(demoDirectBuffer, 0, demoArray)
        val readArray = StringArrayConverter.converter.readWithoutLength(demoDirectBuffer, 0, demoArray.size)

        assert(demoArray.contentEquals(readArray))
    }
}