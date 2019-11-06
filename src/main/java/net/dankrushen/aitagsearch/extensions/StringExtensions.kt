package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.database.DatabaseUtils
import org.agrona.DirectBuffer
import org.agrona.concurrent.UnsafeBuffer

fun String.toUnsafeBuffer(size: Int): UnsafeBuffer {
    return DatabaseUtils.stringToUnsafeBuffer(this, size)
}

fun DirectBuffer.toUTF8String(): String {
    return DatabaseUtils.directBufferToString(this)
}