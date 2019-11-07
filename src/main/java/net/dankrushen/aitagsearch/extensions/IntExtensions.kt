package net.dankrushen.aitagsearch.extensions

import net.dankrushen.aitagsearch.database.DatabaseUtils
import org.agrona.concurrent.UnsafeBuffer

fun Int.toUnsafeBuffer(): UnsafeBuffer {
    return DatabaseUtils.intToUnsafeBuffer(this)
}