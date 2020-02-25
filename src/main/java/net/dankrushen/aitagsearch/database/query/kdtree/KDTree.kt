/*
 * Copyright 2016 Thomas Jungblut
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dankrushen.aitagsearch.database.query.kdtree

import net.dankrushen.aitagsearch.comparison.DistanceMeasurer
import net.dankrushen.aitagsearch.comparison.EuclidianDistance
import net.dankrushen.aitagsearch.datatypes.FloatVector

class KDTree<K>(val distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer) {
    data class TreeNode<K>(val splitDimension: Int, val vector: FloatVector, val value: K) {
        var left: TreeNode<K>? = null
        var right: TreeNode<K>? = null

        fun splitValue(): Float {
            return vector[splitDimension]
        }
    }

    private val treeNodes: MutableList<TreeNode<K>> = mutableListOf()
    private var root: TreeNode<K>? = null
    private var size = 0

    fun getSplitDimension(vec: FloatVector, level: Int): Int {
        return (level + 1) % vec.dimension
    }

    fun add(vec: FloatVector, value: K) {
        val treeRoot = root
        require(vec.dimension > 0)

        // shortcut for empty tree
        if (treeRoot == null) {
            root = TreeNode(getSplitDimension(vec, 0), vec, value)
            size++
            return
        }

        require(vec.dimension == treeRoot.vector.dimension)

        var current: TreeNode<K> = treeRoot
        var level = 0
        var right: Boolean

        // traverse the tree to the free spot that matches the dimension
        while (true) {
            right = current.splitValue() <= vec[current.splitDimension]
            val next = if (right) current.right else current.left
            current = next ?: break
            level++
        }

        val splitDimension: Int = getSplitDimension(vec, level)

        // do the "real" insert
        // note that current in this case is the parent
        val node = TreeNode(splitDimension, vec, value)

        treeNodes.add(node)

        if (right) {
            current.right = node
        } else {
            current.left = node
        }

        size++
    }

    fun balance() {
        treeNodes.sortBy { treeNode ->
            treeNode.vector[treeNode.splitDimension]
        }

        // do an inverse binary search to build up the tree from the root
        root = fix(treeNodes, 0, treeNodes.size - 1)
    }

    /**
     * Fix up the tree recursively by divide and conquering the sorted array.
     */
    private fun fix(nodes: MutableList<TreeNode<K>>, start: Int, end: Int): TreeNode<K>? {
        return if (start > end) {
            null
        } else {
            val mid = start + end ushr 1
            val midNode = nodes[mid]

            midNode.left = fix(nodes, start, mid - 1)
            midNode.right = fix(nodes, mid + 1, end)

            midNode
        }
    }

    private fun addIfSmaller(keyVectorDists: Array<Pair<Pair<K, FloatVector>, Float>?>, key: K, vector: FloatVector, dist: Float, condition: ((key: K) -> Boolean)?): Float? {
        var maxKeyVectorIndex = 0

        for (i in 1 until keyVectorDists.size) {
            val maxKeyVector = keyVectorDists[maxKeyVectorIndex] ?: break
            val curKeyVector = keyVectorDists[i]

            if (curKeyVector == null || maxKeyVector.second < curKeyVector.second) {
                maxKeyVectorIndex = i
            }
        }

        val maxKeyVector = keyVectorDists[maxKeyVectorIndex]

        if (maxKeyVector == null || dist < maxKeyVector.second) {
            // If the condition is either null or true (not false)
            if (condition?.invoke(key) != false)
                keyVectorDists[maxKeyVectorIndex] = Pair(Pair(key, vector), dist)
        }

        return maxKeyVector?.second
    }

    fun getNeighbours(vector: FloatVector, numNeighbours: Int, condition: ((key: K) -> Boolean)? = null, maxDist: Float? = null, minDist: Float? = null): Array<Pair<Pair<K, FloatVector>, Float>?> {
        val treeRoot = root ?: throw IllegalStateException("The tree root node must have a value")

        val vectorDiffsList: Array<Pair<Pair<K, FloatVector>, Float>?> = arrayOfNulls(numNeighbours)
        val hyperRectangle = HyperRectangle.infiniteHyperRectangle(vector.dimension)

        getNeighboursInternal(treeRoot, vector, hyperRectangle, condition, maxDist, minDist, vectorDiffsList)
        vectorDiffsList.sortBy { keyVectorDist -> keyVectorDist?.second }

        return vectorDiffsList
    }

    private fun getNeighboursInternal(current: TreeNode<K>, vector: FloatVector, leftHyperRectangle: HyperRectangle,
                                      condition: ((key: K) -> Boolean)?, maxDist: Float?, minDist: Float?,
                                      keyVectorDists: Array<Pair<Pair<K, FloatVector>, Float>?>): Float? {
        var internalMaxDist = maxDist

        val splitDim = current.splitDimension
        val pivot: FloatVector = current.vector

        val rightHyperRectangle = HyperRectangle(leftHyperRectangle.min.clone(), leftHyperRectangle.max.clone())

        leftHyperRectangle.max[splitDim] = pivot[splitDim]
        rightHyperRectangle.min[splitDim] = pivot[splitDim]

        val nearestNode: TreeNode<K>?
        val nearestHyperRectangle: HyperRectangle?

        val furthestNode: TreeNode<K>?
        val furthestHyperRectangle: HyperRectangle?

        // If left is nearest
        if (vector[splitDim] > pivot[splitDim]) {
            nearestNode = current.left
            nearestHyperRectangle = leftHyperRectangle

            furthestNode = current.right
            furthestHyperRectangle = rightHyperRectangle
        } else {
            nearestNode = current.right
            nearestHyperRectangle = rightHyperRectangle

            furthestNode = current.left
            furthestHyperRectangle = leftHyperRectangle
        }

        if (nearestNode != null) {
            val newMaxDist = getNeighboursInternal(nearestNode, vector, nearestHyperRectangle, condition, internalMaxDist, minDist, keyVectorDists)

            if (newMaxDist != null)
                internalMaxDist = newMaxDist
        }

        val closestDistance = distanceMeasurer.calcDistance(furthestHyperRectangle.closestPoint(vector), vector)
        if ((internalMaxDist == null || closestDistance < internalMaxDist) && (minDist == null || closestDistance > minDist)) {

            val distancePivotToTarget: Float = distanceMeasurer.calcDistance(pivot, vector)
            if ((internalMaxDist == null || distancePivotToTarget < internalMaxDist) && (minDist == null || distancePivotToTarget > minDist)) {
                val newMaxDist = addIfSmaller(keyVectorDists, current.value, current.vector, distancePivotToTarget, condition)

                if (newMaxDist != null)
                    internalMaxDist = newMaxDist
            }

            // Check furthest node
            if (furthestNode != null) {
                val newMaxDist = getNeighboursInternal(furthestNode, vector, furthestHyperRectangle, condition, internalMaxDist, minDist, keyVectorDists)

                if (newMaxDist != null)
                    internalMaxDist = newMaxDist
            }
        }

        return internalMaxDist
    }
}