package net.dankrushen.aitagsearch.database.query

import net.dankrushen.aitagsearch.comparison.DistanceMeasurer
import net.dankrushen.aitagsearch.comparison.EuclidianDistance
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.lmdbjava.Txn

class BruteNearestNeighbour<K>(val db: TypedPairDatabase<K, FloatVector>, val distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer) {

    private fun addIfSmaller(keyVectorDists: Array<Pair<Pair<K, FloatVector>, Float>?>, rawKey: DirectBuffer, vector: FloatVector, dist: Float, condition: ((key: K) -> Boolean)?): Float? {
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
            val key = db.keyConverter.read(rawKey, 0)

            // If the condition is either null or true (not false)
            if (condition?.invoke(key) != false)
                keyVectorDists[maxKeyVectorIndex] = Pair(Pair(key, vector), dist)
        }

        return maxKeyVector?.second
    }

    fun getNeighbours(txn: Txn<DirectBuffer>, vector: FloatVector, numNeighbours: Int, condition: ((key: K) -> Boolean)? = null, maxDist: Float? = null, minDist: Float? = null): Array<Pair<Pair<K, FloatVector>, Float>?> {
        require(numNeighbours > 0) { "\"numNeighbours\" must be a value greater than 0" }

        val vectorDiffsList = arrayOfNulls<Pair<Pair<K, FloatVector>, Float>>(numNeighbours)

        var internalMaxDist: Float? = maxDist
        db.dbi.iterate(txn).use {
            for (keyVal in it) {
                val entryVector = if (db.valueLength != null) db.valueConverter.readWithoutLength(keyVal.`val`(), db.valueIndex, db.valueLength) else db.valueConverter.read(keyVal.`val`(), db.valueIndex)
                val dist = distanceMeasurer.calcDistance(vector, entryVector)

                if ((internalMaxDist == null || dist < internalMaxDist!!) && (minDist == null || dist > minDist)) {
                    val newMaxDist = addIfSmaller(vectorDiffsList, keyVal.key(), entryVector, dist, condition)

                    if (newMaxDist != null)
                        internalMaxDist = newMaxDist
                }
            }
        }

        vectorDiffsList.sortBy { keyVectorDist -> keyVectorDist?.second }

        return vectorDiffsList
    }
}