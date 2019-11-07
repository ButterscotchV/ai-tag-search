package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable

open class PairDatabase(val env: Env<DirectBuffer>, val dbName: String) : Closeable {
    val db: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    fun putRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer, value: DirectBuffer, commitTxn: Boolean = false) {
        db.put(txn, key, value)

        if (commitTxn)
            txn.commit()
    }

    fun putRawPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<DirectBuffer, DirectBuffer>, commitTxn: Boolean = false) {
        putRawPair(txn, keyValuePair.first, keyValuePair.second, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, converters: Pair<DirectBufferConverter<K>, DirectBufferConverter<V>>, commitTxn: Boolean = false) {
        putPair(txn, keyValuePair.first, keyValuePair.second, converters, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, key: K, value: V, converters: Pair<DirectBufferConverter<K>, DirectBufferConverter<V>>, commitTxn: Boolean = false) {
        val rawKey = converters.first.toDirectBuffer(key)
        val rawValue = converters.second.toDirectBuffer(value)

        putRawPair(txn, rawKey, rawValue, commitTxn)
    }

    fun getRawValue(txn: Txn<DirectBuffer>, key: DirectBuffer): DirectBuffer? {
        return db.get(txn, key)
    }

    fun <K, V> getValue(txn: Txn<DirectBuffer>, key: K, converters: Pair<DirectBufferConverter<K>, DirectBufferConverter<V>>): V? {
        val rawKey = converters.first.toDirectBuffer(key)
        val rawValue = getRawValue(txn, rawKey) ?: return null

        return converters.second.read(rawValue, 0)
    }

    fun getRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer): Pair<DirectBuffer, DirectBuffer>? {
        val rawValue = getRawValue(txn, key) ?: return null

        return Pair(key, rawValue)
    }

    fun <K, V> getPair(txn: Txn<DirectBuffer>, key: K, converters: Pair<DirectBufferConverter<K>, DirectBufferConverter<V>>): Pair<K, V>? {
        val value = getValue(txn, key, converters) ?: return null

        return Pair(key, value)
    }

    override fun close() {
        db.close()
    }
}