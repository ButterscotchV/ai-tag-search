package net.dankrushen.aitagsearch.conversion

import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class StringArrayConverter : DirectBufferConverter<Array<String>>() {
    companion object {
        val converter = StringArrayConverter()
    }

    override fun getLength(value: Array<String>): Int {
        return value.size
    }

    override fun getSize(value: Array<String>): Int {
        return value.sumBy { string -> StringConverter.converter.getSizeWithCount(string) }
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: Array<String>): Int {
        var bytesWritten = 0

        for (string in value) {
            bytesWritten += StringConverter.converter.write(directBuffer, index + bytesWritten, string)
        }

        return bytesWritten
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<Array<String>, Int> {
        var bytesRead = 0

        val stringList = mutableListOf<String>()

        for (i in 0 until length) {
            val results = StringConverter.converter.readCount(directBuffer, index + bytesRead)
            stringList.add(results.first)
            bytesRead += results.second
        }

        return Pair(stringList.toTypedArray(), bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): Array<String> {
        var bytesRead = 0

        val stringList = mutableListOf<String>()

        for (i in 0 until length) {
            val results = StringConverter.converter.readCount(directBuffer, index + bytesRead)
            stringList.add(results.first)
            bytesRead += results.second
        }

        return stringList.toTypedArray()
    }
}