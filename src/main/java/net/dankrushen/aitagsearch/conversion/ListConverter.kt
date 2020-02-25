package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.conversion.basetypes.FloatConverter
import net.dankrushen.aitagsearch.conversion.basetypes.IntConverter
import net.dankrushen.aitagsearch.conversion.basetypes.StringConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class ListConverter<T>(val converter: DirectBufferConverter<T>, val valueLength: Int? = null) : DirectBufferConverter<List<T>>() {
    companion object {
        val floatListConverter = ListConverter(FloatConverter.converter)
        val intListConverter = ListConverter(IntConverter.converter)
        val stringListConverter = ListConverter(StringConverter.converter)
    }

    override fun getLength(value: List<T>): Int = value.size

    override fun getSize(value: List<T>): Int {
        return if (valueLength != null) {
            value.sumBy { entry -> converter.getSize(entry) }
        } else {
            value.sumBy { entry -> converter.getSizeWithCount(entry) }
        }
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: List<T>): Int {
        var bytesWritten = 0

        for (entry in value) {
            val curIndex = index + bytesWritten
            bytesWritten += if (valueLength != null) {
                converter.writeWithoutLength(directBuffer, curIndex, entry)
            } else {
                converter.write(directBuffer, curIndex, entry)
            }
        }

        return bytesWritten
    }

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<MutableList<T>, Int> {
        var bytesRead = 0

        val entryList = mutableListOf<T>()

        for (i in 0 until length) {
            val curIndex = index + bytesRead
            val results = if (valueLength != null) {
                converter.readWithoutLengthCount(directBuffer, curIndex, valueLength)
            } else {
                converter.readCount(directBuffer, curIndex)
            }

            entryList.add(results.first)
            bytesRead += results.second
        }

        return Pair(entryList, bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): MutableList<T> {
        var bytesRead = 0

        val entryList = mutableListOf<T>()

        for (i in 0 until length) {
            val curIndex = index + bytesRead
            val results = if (valueLength != null) {
                converter.readWithoutLengthCount(directBuffer, curIndex, valueLength)
            } else {
                converter.readCount(directBuffer, curIndex)
            }

            entryList.add(results.first)
            bytesRead += results.second
        }

        return entryList
    }
}