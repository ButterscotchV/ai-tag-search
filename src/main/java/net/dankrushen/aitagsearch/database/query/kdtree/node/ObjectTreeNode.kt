package net.dankrushen.aitagsearch.database.query.kdtree.node

import net.dankrushen.aitagsearch.datatypes.FloatVector

class ObjectTreeNode<K>(value: K, splitIndex: Int, override val vector: FloatVector) : TreeNode<K>(value, splitIndex, vector[splitIndex])