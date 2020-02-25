package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class NormalCosineDistance : DistanceMeasurer {
    companion object {
        val measurer = NormalCosineDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        return 1f - firstVector.dot(secondVector)
    }
}