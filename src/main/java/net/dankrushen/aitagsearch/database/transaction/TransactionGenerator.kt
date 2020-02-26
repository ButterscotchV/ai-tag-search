package net.dankrushen.aitagsearch.database.transaction

import org.agrona.DirectBuffer
import org.lmdbjava.Env
import org.lmdbjava.Txn

interface TransactionGenerator {
    val env: Env<DirectBuffer>

    fun <T> txnRead(block: (txn: Txn<DirectBuffer>) -> T): T
    fun <T> txnWrite(block: (txn: Txn<DirectBuffer>) -> T): T
}