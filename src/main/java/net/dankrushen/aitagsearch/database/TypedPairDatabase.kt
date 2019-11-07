package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.lmdbjava.Env
import org.lmdbjava.Txn

class TypedPairDatabase<K, V>(env: Env<DirectBuffer>, dbName: String, val keyConverter: DirectBufferConverter<K>, val valueConverter: DirectBufferConverter<V>): PairDatabase(env, dbName) {

    fun putPair(txn: Txn<DirectBuffer>, key: K, value: V, commitTxn: Boolean = false) {
        putPair(txn, key, value, keyConverter, valueConverter, commitTxn)
    }

    fun putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, commitTxn: Boolean = false) {
        putPair(txn, keyValuePair.first, keyValuePair.second, keyConverter, valueConverter, commitTxn)
    }

    fun getValue(txn: Txn<DirectBuffer>, key: K): V? {
        return getValue(txn, key, keyConverter, valueConverter)
    }

    fun getPair(txn: Txn<DirectBuffer>, key: K): Pair<K, V>? {
        return getPair(txn, key, keyConverter, valueConverter)
    }
}