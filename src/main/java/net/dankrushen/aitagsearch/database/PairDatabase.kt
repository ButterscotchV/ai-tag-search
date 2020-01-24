package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseKeyIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseValueIterator
import org.agrona.DirectBuffer
import org.agrona.concurrent.UnsafeBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable
import java.nio.ByteBuffer

open class PairDatabase(val env: Env<DirectBuffer>, val dbName: String, var commitTxnByDef: Boolean = false) : Closeable {

    val dbi: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    val syncKeyByteBuffer = ByteBuffer.allocateDirect(env.maxKeySize)
    val syncKeyDirectBuffer = UnsafeBuffer(syncKeyByteBuffer)

    fun <K> keyToDirectBuffer(key: K, keyConverter: DirectBufferConverter<K>): DirectBuffer {
        syncKeyDirectBuffer.wrap(syncKeyByteBuffer, 0, env.maxKeySize)
        val bytesWritten = keyConverter.write(syncKeyDirectBuffer, 0, key)
        syncKeyDirectBuffer.wrap(syncKeyByteBuffer, 0, bytesWritten)

        return syncKeyDirectBuffer
    }

    fun <V> valueToDirectBuffer(value: V, valueConverter: DirectBufferConverter<V>): DirectBuffer {
        return valueConverter.toDirectBuffer(value)
    }

    fun <K, V> iterate(txn: Txn<DirectBuffer>, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): TypedPairDatabaseIterator<K, V> {
        return TypedPairDatabaseIterator(this, txn, keyConverter, valueConverter)
    }

    fun <K> iterateKeys(txn: Txn<DirectBuffer>, keyConverter: DirectBufferConverter<K>): TypedPairDatabaseKeyIterator<K> {
        return TypedPairDatabaseKeyIterator(this, txn, keyConverter)
    }

    fun <V> iterateValues(txn: Txn<DirectBuffer>, valueConverter: DirectBufferConverter<V>): TypedPairDatabaseValueIterator<V> {
        return TypedPairDatabaseValueIterator(this, txn, valueConverter)
    }

    fun putRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer, value: DirectBuffer, commitTxn: Boolean = commitTxnByDef) {
        dbi.put(txn, key, value)

        if (commitTxn)
            txn.commit()
    }

    fun putRawPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<DirectBuffer, DirectBuffer>, commitTxn: Boolean = commitTxnByDef) {
        putRawPair(txn, keyValuePair.first, keyValuePair.second, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, key: K, value: V, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>, commitTxn: Boolean = commitTxnByDef) {
        val rawKey = keyToDirectBuffer(key, keyConverter)
        val rawValue = valueToDirectBuffer(value, valueConverter)

        putRawPair(txn, rawKey, rawValue, commitTxn)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>, commitTxn: Boolean = commitTxnByDef) {
        putPair(txn, keyValuePair.first, keyValuePair.second, keyConverter, valueConverter, commitTxn)
    }

    fun getRawValue(txn: Txn<DirectBuffer>, key: DirectBuffer): DirectBuffer? {
        return dbi.get(txn, key)
    }

    fun <K, V> getValue(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): V? {
        val rawKey = keyToDirectBuffer(key, keyConverter)
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

    fun deleteRaw(txn: Txn<DirectBuffer>, key: DirectBuffer, commitTxn: Boolean = commitTxnByDef) {
        dbi.delete(txn, key)

        if (commitTxn)
            txn.commit()
    }

    fun <K> delete(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, commitTxn: Boolean = commitTxnByDef) {
        val rawKey = keyToDirectBuffer(key, keyConverter)

        deleteRaw(txn, rawKey, commitTxn)
    }

    override fun close() {
        dbi.close()
    }
}