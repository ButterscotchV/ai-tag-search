package net.dankrushen.aitagsearch.database

import net.dankrushen.aitagsearch.extensions.toUnsafeBuffer
import org.agrona.DirectBuffer
import org.lmdbjava.Dbi
import org.lmdbjava.DbiFlags
import org.lmdbjava.Env
import org.lmdbjava.Txn
import java.io.Closeable

class IntStringDatabase(val env: Env<DirectBuffer>, val dbName: String) : Closeable {
    val db: Dbi<DirectBuffer> = env.openDbi(dbName, DbiFlags.MDB_CREATE)

    fun putPair(txn: Txn<DirectBuffer>, pair: Pair<Int, String>, commitTxn: Boolean = false) {
        db.put(txn, pair.first.toUnsafeBuffer(), pair.second.toUnsafeBuffer())

        if (commitTxn)
            txn.commit()
    }

    fun getRawValue(txn: Txn<DirectBuffer>, key: Int): DirectBuffer? {
        return db.get(txn, key.toUnsafeBuffer())
    }

    fun getValue(txn: Txn<DirectBuffer>, key: Int): String? {
        return getRawValue(txn, key)?.getStringUtf8(0)
    }

    fun getPair(txn: Txn<DirectBuffer>, key: Int): Pair<Int, String>? {
        val vector = getValue(txn, key) ?: return null

        return Pair(key, vector)
    }

    override fun close() {
        db.close()
    }
}