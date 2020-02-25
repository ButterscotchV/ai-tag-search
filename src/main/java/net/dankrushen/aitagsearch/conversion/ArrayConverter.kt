package net.dankrushen.aitagsearch.conversion

import net.dankrushen.aitagsearch.conversion.basetypes.FloatConverter
import net.dankrushen.aitagsearch.conversion.basetypes.IntConverter
import net.dankrushen.aitagsearch.conversion.basetypes.StringConverter
import net.dankrushen.aitagsearch.conversion.listconversion.FloatListToArrayConverter
import net.dankrushen.aitagsearch.conversion.listconversion.IntListToArrayConverter
import net.dankrushen.aitagsearch.conversion.listconversion.ListToArrayConverter
import net.dankrushen.aitagsearch.conversion.listconversion.StringListToArrayConverter
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer

class ArrayConverter<T>(val converter: DirectBufferConverter<T>, val listToArrayConverter: ListToArrayConverter<T>, val valueLength: Int? = null) : DirectBufferConverter<Array<T>>() {
    companion object {
        val floatArrayConverter = ArrayConverter(FloatConverter.converter, FloatListToArrayConverter.converter)
        val intArrayConverter = ArrayConverter(IntConverter.converter, IntListToArrayConverter.converter)
        val stringArrayConverter = ArrayConverter(StringConverter.converter, StringListToArrayConverter.converter)
    }

    override fun getLength(value: Array<T>): Int = value.size

    override fun getSize(value: Array<T>): Int {
        return if (valueLength != null) {
            value.sumBy { entry -> converter.getSize(entry) }
        } else {
            value.sumBy { entry -> converter.getSizeWithCount(entry) }
        }
    }

    override fun writeWithoutLength(directBuffer: MutableDirectBuffer, index: Int, value: Array<T>): Int {
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

    override fun readWithoutLengthCount(directBuffer: DirectBuffer, index: Int, length: Int): Pair<Array<T>, Int> {
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

        return Pair(listToArrayConverter.listToArray(entryList), bytesRead)
    }

    override fun readWithoutLength(directBuffer: DirectBuffer, index: Int, length: Int): Array<T> {
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

        return listToArrayConverter.listToArray(entryList)
    }
}