package net.dankrushen.aitagsearch.database.query.kdtree.node

import net.dankrushen.aitagsearch.datatypes.FloatVector

abstract class TreeNode<K>(open val value: K, val splitIndex: Int, val splitValue: Float) {
    var left: TreeNode<K>? = null
    var right: TreeNode<K>? = null

    abstract val vector: FloatVector
}