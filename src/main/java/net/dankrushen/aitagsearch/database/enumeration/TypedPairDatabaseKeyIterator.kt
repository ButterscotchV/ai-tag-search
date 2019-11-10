package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseKeyIterator<K>(db: PairDatabase, txn: Txn<DirectBuffer>, val keyConverter: DirectBufferConverter<K>) : Iterator<K>, Closeable {

    val rawCursorIterator = db.dbi.iterate(txn)
    val rawIterator = rawCursorIterator.iterator()

    override fun hasNext(): Boolean {
        return rawIterator.hasNext()
    }

    override fun next(): K {
        val nextRawPair = rawIterator.next()

        return keyConverter.read(nextRawPair.key(), 0)
    }

    override fun close() {
        rawCursorIterator.close()
    }
}