package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class LinearDistance : DistanceComparer {
    companion object {
        val comparer = LinearDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        return firstVector.distanceTo(secondVector)
    }
}