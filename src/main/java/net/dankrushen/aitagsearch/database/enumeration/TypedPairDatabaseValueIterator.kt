package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseValueIterator<V>(val db: PairDatabase, txn: Txn<DirectBuffer>, val valueConverter: DirectBufferConverter<V>) : Iterator<V>, Closeable {

    constructor(db: TypedPairDatabase<*, V>, txn: Txn<DirectBuffer>) : this(db, txn, db.valueConverter)

    private val rawCursorIterable = db.dbi.iterate(txn)
    private val rawCursorIterator = rawCursorIterable.iterator()

    override fun hasNext(): Boolean {
        return rawCursorIterator.hasNext()
    }

    override fun next(): V {
        val nextRawPair = rawCursorIterator.next()

        return db.valueFromDirectBuffer(nextRawPair.`val`(), valueConverter)
    }

    override fun close() {
        rawCursorIterable.close()
    }
}