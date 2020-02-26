package net.dankrushen.aitagsearch.database.query.kdtree.node

import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.datatypes.FloatVector

open class DatabaseTreeNodeGenerator<K>(val database: TypedPairDatabase<K, FloatVector>) : TreeNodeGenerator<K>() {

    override fun generateTreeNode(value: K, splitIndex: Int, vector: FloatVector): TreeNode<K> {
        return DatabaseTreeNode(database, value, splitIndex, vector[splitIndex])
    }
}