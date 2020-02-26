package net.dankrushen.aitagsearch.database

import org.agrona.DirectBuffer
import org.lmdbjava.Txn

fun <T> PairDatabase.txnRead(block: (txn: Txn<DirectBuffer>) -> T): T {
    return transactionGenerator.txnRead(block)
}

fun <T> PairDatabase.txnWrite(block: (txn: Txn<DirectBuffer>) -> T): T {
    return transactionGenerator.txnWrite(block)
}