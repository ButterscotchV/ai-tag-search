package net.dankrushen.aitagsearch.database.enumeration

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.PairDatabase
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import org.agrona.DirectBuffer
import org.lmdbjava.Txn
import java.io.Closeable

class TypedPairDatabaseIterator<K, V>(val db: PairDatabase, txn: Txn<DirectBuffer>, val keyConverter: DirectBufferConverter<K>, val valueConverter: DirectBufferConverter<V>) : Iterator<Pair<K, V>>, Closeable {

    constructor(db: TypedPairDatabase<K, V>, txn: Txn<DirectBuffer>) : this(db, txn, db.keyConverter, db.valueConverter)

    val rawCursorIterator = db.dbi.iterate(txn)
    val rawIterator = rawCursorIterator.iterator()

    override fun hasNext(): Boolean {
        return rawIterator.hasNext()
    }

    override fun next(): Pair<K, V> {
        val nextRawPair = rawIterator.next()

        val key = keyConverter.read(nextRawPair.key(), 0)
        val value = if (db.valueLength != null) {
            valueConverter.readWithoutLength(nextRawPair.`val`(), db.valueIndex, db.valueLength)
        } else {
            valueConverter.read(nextRawPair.`val`(), db.valueIndex)
        }

        return Pair(key, value)
    }

    override fun close() {
        rawCursorIterator.close()
    }
}