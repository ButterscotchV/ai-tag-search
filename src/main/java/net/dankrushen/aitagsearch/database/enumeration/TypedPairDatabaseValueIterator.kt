package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseValueIterator<V>(val db: PairDatabase, txn: Txn<DirectBuffer>, val valueConverter: DirectBufferConverter<V>) : Iterator<V>, Closeable {

    val rawCursorIterator = db.dbi.iterate(txn)
    val rawIterator = rawCursorIterator.iterator()

    override fun hasNext(): Boolean {
        return rawIterator.hasNext()
    }

    override fun next(): V {
        val nextRawPair = rawIterator.next()

        return if (db.valueLength != null) {
            valueConverter.readWithoutLength(nextRawPair.`val`(), db.valueIndex, db.valueLength)
        } else {
            valueConverter.read(nextRawPair.`val`(), db.valueIndex)
        }
    }

    override fun close() {
        rawCursorIterator.close()
    }
}