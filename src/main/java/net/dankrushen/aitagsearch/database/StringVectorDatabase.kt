package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.datatypes.FloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVectorWithoutLength
import net.dankrushen.aitagsearch.extensions.toUnsafeBuffer
import org.agrona.DirectBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable

class StringVectorDatabase(val env: Env<DirectBuffer>, val dbName: String) : Closeable {
    val db: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    fun putKeyVector(txn: Txn<DirectBuffer>, keyVector: Pair<String, FloatVector>, commitTxn: Boolean = false) {
        db.put(txn, keyVector.first.toUnsafeBuffer(env.maxKeySize), keyVector.second.toUnsafeBuffer())

        if (commitTxn)
            txn.commit()
    }

    fun getValue(txn: Txn<DirectBuffer>, key: String): DirectBuffer? {
        return db.get(txn, key.toUnsafeBuffer(env.maxKeySize))
    }

    fun getFloatVector(txn: Txn<DirectBuffer>, key: String): FloatVector? {
        return getValue(txn, key)?.getFloatVector(0)
    }

    fun getFloatVectorWithoutLength(txn: Txn<DirectBuffer>, key: String, length: Int): FloatVector? {
        return getValue(txn, key)?.getFloatVectorWithoutLength(0, length)
    }

    fun getKeyVector(txn: Txn<DirectBuffer>, key: String): Pair<String, FloatVector>? {
        val vector = getFloatVector(txn, key) ?: return null

        return Pair(key, vector)
    }

    fun getKeyVectorWithoutLength(txn: Txn<DirectBuffer>, key: String, length: Int): Pair<String, FloatVector>? {
        val vector = getFloatVectorWithoutLength(txn, key, length) ?: return null

        return Pair(key, vector)
    }

    override fun close() {
        db.close()
    }
}