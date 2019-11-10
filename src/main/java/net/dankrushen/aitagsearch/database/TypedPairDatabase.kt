package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseKeyIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseValueIterator
import org.agrona.DirectBuffer
import org.lmdbjava.Env
import org.lmdbjava.Txn

class TypedPairDatabase<K, V>(env: Env<DirectBuffer>, dbName: String, val keyConverter: DirectBufferConverter<K>, val valueConverter: DirectBufferConverter<V>, commitTxnByDef: Boolean = false) : PairDatabase(env, dbName, commitTxnByDef) {

    fun iterate(txn: Txn<DirectBuffer>): TypedPairDatabaseIterator<K, V> {
        return TypedPairDatabaseIterator(this, txn)
    }

    fun iterateKeys(txn: Txn<DirectBuffer>): TypedPairDatabaseKeyIterator<K> {
        return TypedPairDatabaseKeyIterator(this, txn, this.keyConverter)
    }

    fun iterateValues(txn: Txn<DirectBuffer>): TypedPairDatabaseValueIterator<V> {
        return TypedPairDatabaseValueIterator(this, txn, this.valueConverter)
    }

    fun putPair(txn: Txn<DirectBuffer>, key: K, value: V, commitTxn: Boolean = commitTxnByDef) {
        putPair(txn, key, value, keyConverter, valueConverter, commitTxn)
    }

    fun putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, commitTxn: Boolean = commitTxnByDef) {
        putPair(txn, keyValuePair.first, keyValuePair.second, keyConverter, valueConverter, commitTxn)
    }

    fun getValue(txn: Txn<DirectBuffer>, key: K): V? {
        return getValue(txn, key, keyConverter, valueConverter)
    }

    fun getPair(txn: Txn<DirectBuffer>, key: K): Pair<K, V>? {
        return getPair(txn, key, keyConverter, valueConverter)
    }
}