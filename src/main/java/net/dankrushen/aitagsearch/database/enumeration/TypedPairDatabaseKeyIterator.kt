package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseKeyIterator<K>(db: PairDatabase, txn: Txn<DirectBuffer>, val keyConverter: DirectBufferConverter<K>) : Iterator<K>, Closeable {

    constructor(db: TypedPairDatabase<K, *>, txn: Txn<DirectBuffer>) : this(db, txn, db.keyConverter)

    private val rawCursorIterable = db.dbi.iterate(txn)
    private val rawCursorIterator = rawCursorIterable.iterator()

    override fun hasNext(): Boolean {
        return rawCursorIterator.hasNext()
    }

    override fun next(): K {
        val nextRawPair = rawCursorIterator.next()

        return keyConverter.read(nextRawPair.key(), 0)
    }

    override fun close() {
        rawCursorIterable.close()
    }
}