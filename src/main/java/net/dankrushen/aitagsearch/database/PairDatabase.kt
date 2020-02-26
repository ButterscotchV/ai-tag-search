package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseKeyIterator
import net.dankrushen.aitagsearch.database.enumeration.TypedPairDatabaseValueIterator
import net.dankrushen.aitagsearch.database.transaction.TransactionGenerator
import org.agrona.DirectBuffer
import org.agrona.MutableDirectBuffer
import org.agrona.concurrent.UnsafeBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable
import java.nio.ByteBuffer


open class PairDatabase(val env: Env<DirectBuffer>, val dbName: String, val transactionGenerator: TransactionGenerator, val valueIndex: Int = 0, val valueLength: Int? = null) : Closeable {

    val dbi: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    val keyBuffers = ThreadLocal.withInitial { UnsafeBuffer(ByteBuffer.allocateDirect(env.maxKeySize)) }

    fun <K> keyToDirectBuffer(key: K, keyConverter: DirectBufferConverter<K>): DirectBuffer {
        val keyBuffer = keyBuffers.get()

        keyBuffer.wrap(keyBuffer.byteBuffer(), 0, env.maxKeySize)
        val bytesWritten = keyConverter.write(keyBuffer, 0, key)
        keyBuffer.wrap(keyBuffer.byteBuffer(), 0, bytesWritten)

        return keyBuffer
    }

    fun <V> valueToDirectBuffer(value: V, valueConverter: DirectBufferConverter<V>): MutableDirectBuffer {
        return if (valueLength != null) {
            valueConverter.toDirectBufferWithoutLength(valueIndex, value)
        } else {
            valueConverter.toDirectBuffer(valueIndex, value)
        }
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

    fun putRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer, value: DirectBuffer) {
        dbi.put(txn, key, value)
    }

    fun putRawPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<DirectBuffer, DirectBuffer>) {
        putRawPair(txn, keyValuePair.first, keyValuePair.second)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, key: K, value: V, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>) {
        val rawKey = keyToDirectBuffer(key, keyConverter)
        val rawValue = valueToDirectBuffer(value, valueConverter)

        putRawPair(txn, rawKey, rawValue)
    }

    fun <K, V> putPair(txn: Txn<DirectBuffer>, keyValuePair: Pair<K, V>, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>) {
        putPair(txn, keyValuePair.first, keyValuePair.second, keyConverter, valueConverter)
    }

    fun getRawValue(txn: Txn<DirectBuffer>, key: DirectBuffer): DirectBuffer? {
        return dbi.get(txn, key)
    }

    fun <K, V> getValue(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): V? {
        val rawKey = keyToDirectBuffer(key, keyConverter)
        val rawValue = getRawValue(txn, rawKey) ?: return null

        return if (valueLength != null) {
            valueConverter.readWithoutLength(rawValue, valueIndex, valueLength)
        } else {
            valueConverter.read(rawValue, valueIndex)
        }
    }

    fun getRawPair(txn: Txn<DirectBuffer>, key: DirectBuffer): Pair<DirectBuffer, DirectBuffer>? {
        val rawValue = getRawValue(txn, key) ?: return null

        return Pair(key, rawValue)
    }

    fun <K, V> getPair(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>, valueConverter: DirectBufferConverter<V>): Pair<K, V>? {
        val value = getValue(txn, key, keyConverter, valueConverter) ?: return null

        return Pair(key, value)
    }

    fun deleteRaw(txn: Txn<DirectBuffer>, key: DirectBuffer) {
        dbi.delete(txn, key)
    }

    fun <K> delete(txn: Txn<DirectBuffer>, key: K, keyConverter: DirectBufferConverter<K>) {
        val rawKey = keyToDirectBuffer(key, keyConverter)

        deleteRaw(txn, rawKey)
    }

    override fun close() {
        dbi.close()
    }
}