package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

interface DistanceMeasurer {
    fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float

    fun calcDistance(firstPair: Pair<Any, FloatVector>, secondPair: Pair<Any, FloatVector>): Float {
        return calcDistance(firstPair.second, secondPair.second)
    }
}