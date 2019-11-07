package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class CosineDistance : DistanceMeasurer {
    companion object {
        val measurer = CosineDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        val dotProduct = secondVector.dot(firstVector)
        var denominator = firstVector.magnitude * secondVector.magnitude

        // correct for floating-point rounding errors
        if (denominator < dotProduct) {
            denominator = dotProduct
        }

        return if (denominator == 0f) 1f else 1f - dotProduct / denominator

    }
}