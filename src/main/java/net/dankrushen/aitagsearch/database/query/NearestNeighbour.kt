package net.dankrushen.aitagsearch.database.query

import net.dankrushen.aitagsearch.comparison.DistanceMeasurer
import net.dankrushen.aitagsearch.comparison.EuclidianDistance
import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.conversion.FloatVectorConverter
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.lmdbjava.Txn

class NearestNeighbour<K>(val db: TypedPairDatabase<K, FloatVector>, var distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer) {

    internal fun convertRawKeyVectorDist(rawKey: DirectBuffer, vector: FloatVector, dist: Float): Pair<Pair<K, FloatVector>, Float> {
        val key = db.keyConverter.read(rawKey, 0)
        return Pair(Pair(key, vector), dist)
    }

    internal fun addIfSmaller(keyVectorDists: Array<Pair<Pair<K, FloatVector>, Float>?>, rawKey: DirectBuffer, vector: FloatVector, dist: Float): Float? {
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
            keyVectorDists[maxKeyVectorIndex] = convertRawKeyVectorDist(rawKey, vector, dist)
        }

        return maxKeyVector?.second
    }

    fun getNeighbours(txn: Txn<DirectBuffer>, vector: FloatVector, numNeighbours: Int): Array<Pair<Pair<K, FloatVector>, Float>?> {
        require(numNeighbours > 0) { "\"numNeighbours\" must be a value greater than 0" }

        val vectorDiffsList = arrayOfNulls<Pair<Pair<K, FloatVector>, Float>>(numNeighbours)

        var maxDist: Float? = null

        db.dbi.iterate(txn).use {
            for (keyVal in it) {
                val localMaxDist = maxDist

                val entryVector = FloatVectorConverter.converter.read(keyVal.`val`(), 0)
                val dist = distanceMeasurer.calcDistance(vector, entryVector)

                if (localMaxDist == null || dist < localMaxDist) {
                    maxDist = addIfSmaller(vectorDiffsList, keyVal.key(), entryVector, dist)
                }
            }
        }

        vectorDiffsList.sortBy { keyVectorDist -> keyVectorDist?.second }

        return vectorDiffsList
    }
}