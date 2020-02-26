package net.dankrushen.aitagsearch.database.transaction

import org.agrona.DirectBuffer
import org.lmdbjava.Env
import org.lmdbjava.Txn

class ThreadLocalTransactionGenerator(override val env: Env<DirectBuffer>) : TransactionGenerator {

    private val readTxn = ThreadLocal<Txn<DirectBuffer>>()
    private val writeTxn = ThreadLocal<Txn<DirectBuffer>>()

    override fun <T> txnRead(block: (txn: Txn<DirectBuffer>) -> T): T {
        val retrievedTxn: Txn<DirectBuffer>? = readTxn.get()

        val txn = if (retrievedTxn != null) {
            retrievedTxn
        } else {
            val txn: Txn<DirectBuffer> = env.txnRead()
            readTxn.set(txn)

            txn
        }

        try {
            return block(txn)
        } finally {
            if (retrievedTxn == null) {
                try {
                    txn.close()
                } catch (e: Throwable) {
                    // Ignore
                }

                try {
                    readTxn.remove()
                } catch (e: Throwable) {
                    // Ignore
                }
            }
        }
    }

    override fun <T> txnWrite(block: (txn: Txn<DirectBuffer>) -> T): T {
        val retrievedTxn: Txn<DirectBuffer>? = writeTxn.get()

        val txn = if (retrievedTxn != null) {
            retrievedTxn
        } else {
            val txn: Txn<DirectBuffer> = env.txnWrite()
            writeTxn.set(txn)

            txn
        }

        try {
            return block(txn)
        } finally {
            if (retrievedTxn == null) {
                try {
                    txn.close()
                } catch (e: Throwable) {
                    // Ignore
                }

                try {
                    writeTxn.remove()
                } catch (e: Throwable) {
                    // Ignore
                }
            }
        }
    }
}