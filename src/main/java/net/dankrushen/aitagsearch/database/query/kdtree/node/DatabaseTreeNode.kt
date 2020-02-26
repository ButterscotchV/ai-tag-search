package net.dankrushen.aitagsearch.database.query.kdtree.node

import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.database.txnRead
import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer

class DatabaseTreeNode<K>(val database: TypedPairDatabase<K, FloatVector>, value: K, splitIndex: Int, splitValue: Float) : TreeNode<K>(value, splitIndex, splitValue) {

    val valueBytes: DirectBuffer

    init {
        valueBytes = database.keyConverter.toDirectBuffer(0, value)
    }

    override val value: K
        get() = database.keyConverter.read(valueBytes, 0)

    override val vector: FloatVector
        get() {
            val rawValue = database.txnRead { txn ->
                database.getRawValue(txn, valueBytes)!!
            }

            return if (database.valueLength != null) {
                database.valueConverter.readWithoutLength(rawValue, database.valueIndex, database.valueLength)
            } else {
                database.valueConverter.read(rawValue, database.valueIndex)
            }
        }
}