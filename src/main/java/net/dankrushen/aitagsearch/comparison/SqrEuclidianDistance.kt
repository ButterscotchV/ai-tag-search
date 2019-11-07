package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class SqrEuclidianDistance : DistanceMeasurer {
    companion object {
        val measurer = SqrEuclidianDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        return (secondVector - firstVector).sqrMagnitude
    }
}