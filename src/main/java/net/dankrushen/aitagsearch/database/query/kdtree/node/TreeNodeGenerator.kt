package net.dankrushen.aitagsearch.database.query.kdtree.node

import net.dankrushen.aitagsearch.datatypes.FloatVector

open class TreeNodeGenerator<K> {

    open fun generateTreeNode(value: K, splitIndex: Int, vector: FloatVector): TreeNode<K> {
        return ObjectTreeNode(value, splitIndex, vector)
    }
}