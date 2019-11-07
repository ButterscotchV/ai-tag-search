package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.lmdbjava.Env
import org.lmdbjava.Txn

class TypedPairDatabase<K, V>(env: Env<DirectBuffer>, dbName: String, val converters: Pair<DirectBufferConverter<K>, DirectBufferConverter<V>>): PairDatabase(env, dbName) {

    fun putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, commitTxn: Boolean = false) {
        putPair(txn, keyValuePair.first, keyValuePair.second, converters, commitTxn)
    }

    fun putPair(txn: Txn<DirectBuffer>, key: K, value: V, commitTxn: Boolean = false) {
        val rawKey = converters.first.toDirectBuffer(key)
        val rawValue = converters.second.toDirectBuffer(value)

        putRawPair(txn, rawKey, rawValue, commitTxn)
    }

    fun getValue(txn: Txn<DirectBuffer>, key: K): V? {
        val rawKey = converters.first.toDirectBuffer(key)
        val rawValue = getRawValue(txn, rawKey) ?: return null

        return converters.second.read(rawValue, 0)
    }

    fun getPair(txn: Txn<DirectBuffer>, key: K): Pair<K, V>? {
        val value = getValue(txn, key, converters) ?: return null

        return Pair(key, value)
    }
}