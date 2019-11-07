package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class EuclidianDistance : DistanceMeasurer {
    companion object {
        val measurer = EuclidianDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        return firstVector.distanceTo(secondVector)
    }
}