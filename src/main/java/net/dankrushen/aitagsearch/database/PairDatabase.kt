package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import org.agrona.DirectBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable

open class PairDatabase(val env: Env<DirectBuffer>, val dbName: String, var commitTxnByDef: Boolean = false) : Closeable {
    val db: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    fun putRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer, value: DirectBuffer, commitTxn: Boolean = commitTxnByDef) {
        db.put(txn, key, value)

        if (commitTxn)
            txn.commit()
    }

    fun putRawPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<DirectBuffer, DirectBuffer>, commitTxn: Boolean = commitTxnByDef) {
        putRawPair(txn, keyValuePair.first, keyValuePair.second, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, key: K, value: V, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>, commitTxn: Boolean = commitTxnByDef) {
        val rawKey = keyConverter.toDirectBuffer(key)
        val rawValue = valueConverter.toDirectBuffer(value)

        putRawPair(txn, rawKey, rawValue, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>, commitTxn: Boolean = commitTxnByDef) {
        putPair(txn, keyValuePair.first, keyValuePair.second, keyConverter, valueConverter, commitTxn)
    }

    fun getRawValue(txn: Txn<DirectBuffer>, key: DirectBuffer): DirectBuffer? {
        return db.get(txn, key)
    }

    fun <K, V> getValue(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): V? {
        val rawKey = keyConverter.toDirectBuffer(key)
        val rawValue = getRawValue(txn, rawKey) ?: return null

        return valueConverter.read(rawValue, 0)
    }

    fun getRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer): Pair<DirectBuffer, DirectBuffer>? {
        val rawValue = getRawValue(txn, key) ?: return null

        return Pair(key, rawValue)
    }

    fun <K, V> getPair(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): Pair<K, V>? {
        val value = getValue(txn, key, keyConverter, valueConverter) ?: return null

        return Pair(key, value)
    }

    override fun close() {
        db.close()
    }
}