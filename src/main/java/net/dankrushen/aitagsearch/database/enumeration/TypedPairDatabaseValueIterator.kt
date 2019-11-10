package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseValueIterator<V>(db: PairDatabase, txn: Txn<DirectBuffer>, val valueConverter: DirectBufferConverter<V>) : Iterator<V>, Closeable {

    val rawCursorIterator = db.dbi.iterate(txn)
    val rawIterator = rawCursorIterator.iterator()

    override fun hasNext(): Boolean {
        return rawIterator.hasNext()
    }

    override fun next(): V {
        val nextRawPair = rawIterator.next()

        return valueConverter.read(nextRawPair.`val`(), 0)
    }

    override fun close() {
        rawCursorIterator.close()
    }
}