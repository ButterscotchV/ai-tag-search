package net.dankrushen.aitagsearch.database

import org.agrona.DirectBuffer
import org.lmdbjava.Txn

fun TypedPairDatabase<Int, out Any>.getHighestKey(txn: Txn<DirectBuffer>): Int? {
    var highestNum: Int? = null

    this.iterateKeys(txn).use { keyIterator ->
        for (key in keyIterator) {
            if (highestNum == null || key > highestNum!!)
                highestNum = key
        }
    }

    return highestNum
}

fun TypedPairDatabase<Int, out Any>.getLowestKey(txn: Txn<DirectBuffer>): Int? {
    var lowestNum: Int? = null

    this.iterateKeys(txn).use { keyIterator ->
        for (key in keyIterator) {
            if (lowestNum == null || key < lowestNum!!)
                lowestNum = key
        }
    }

    return lowestNum
}