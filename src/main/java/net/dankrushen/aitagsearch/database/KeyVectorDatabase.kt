package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.extensions.toFloatVector
import net.dankrushen.aitagsearch.extensions.toUnsafeBuffer
import net.dankrushen.aitagsearch.types.FloatVector
import net.dankrushen.aitagsearch.types.KeyVector
import org.agrona.DirectBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable

class KeyVectorDatabase(val env: Env<DirectBuffer>, val dbName: String) : Closeable {
    val db: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    fun putKeyVector(txn: Txn<DirectBuffer>, keyVector: KeyVector, commitTxn: Boolean = false) {
        db.put(txn, keyVector.key.toUnsafeBuffer(env.maxKeySize), keyVector.vector.toUnsafeBuffer())

        if (commitTxn)
            txn.commit()
    }

    fun getFloatVector(txn: Txn<DirectBuffer>, key: String): FloatVector? {
        val value = db.get(txn, key.toUnsafeBuffer(env.maxKeySize)) ?: return null

        return value.toFloatVector()
    }

    fun getKeyVector(txn: Txn<DirectBuffer>, key: String): KeyVector? {
        val vector = getFloatVector(txn, key) ?: return null

        return KeyVector(key, vector)
    }

    override fun close() {
        db.close()
    }
}