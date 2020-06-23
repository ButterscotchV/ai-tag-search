package net.dankrushen.aitagsearch.conversion

import org.agrona.concurrent.UnsafeBuffer
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.test.assertEquals

internal fun <E> Collection<E>.contentEquals(other: Collection<E>): Boolean {
    if (this.size != other.size)
        return false

    for (i in this.indices) {
        if (this.elementAt(i) != other.elementAt(i))
            return false
    }

    return true
}

internal class StringListConverterTest {

    val demoList = listOf("Test1", "Test2", "Test3", "Something Else", "Whatever")

    val expectedSize = (Int.SIZE_BYTES * demoList.size) + (5 * 3) + 14 + 8
    val demoDirectBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(expectedSize))

    val converter = ListConverter.stringListConverter

    @Test
    fun readAndWriteWithoutLength() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoList)
        val readList = converter.readWithoutLengthCount(demoDirectBuffer, 0, demoList.size)

        assertEquals(expectedSize, readList.second)
        assert(demoList.contentEquals(readList.first))
    }

    @Test
    fun writeWithoutLength() {
        val bytesWritten = converter.writeWithoutLength(demoDirectBuffer, 0, demoList)

        assertEquals(expectedSize, bytesWritten)
    }

    @Test
    fun readWithoutLengthCount() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoList)
        val readList = converter.readWithoutLengthCount(demoDirectBuffer, 0, demoList.size)

        assertEquals(expectedSize, readList.second)
        assert(demoList.contentEquals(readList.first))
    }

    @Test
    fun readWithoutLength() {
        converter.writeWithoutLength(demoDirectBuffer, 0, demoList)
        val readList = converter.readWithoutLength(demoDirectBuffer, 0, demoList.size)

        assert(demoList.contentEquals(readList))
    }
}