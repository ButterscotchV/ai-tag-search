package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.database.DatabaseUtils
import org.agrona.concurrent.UnsafeBuffer

fun String.toUnsafeBuffer(): UnsafeBuffer {
    return DatabaseUtils.stringToUnsafeBuffer(this)
}

fun String.toUnsafeBuffer(size: Int): UnsafeBuffer {
    return DatabaseUtils.stringToUnsafeBuffer(this, size)
}