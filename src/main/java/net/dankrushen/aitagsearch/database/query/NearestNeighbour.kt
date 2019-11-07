package net.dankrushen.aitagsearch.database.query

import net.dankrushen.aitagsearch.comparison.DistanceMeasurer
import net.dankrushen.aitagsearch.comparison.EuclidianDistance
import net.dankrushen.aitagsearch.conversion.DirectBufferConverter
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.datatypes.FloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVector
import org.agrona.DirectBuffer
import org.lmdbjava.Txn

class NearestNeighbour<K>(val db: TypedPairDatabase<K, FloatVector>, val keyConverter: DirectBufferConverter<K>) {

    internal fun convertRawKeyVectorDist(keyVectorDist: Pair<Pair<DirectBuffer, FloatVector>, Float>): Pair<Pair<K, FloatVector>, Float> {
        val key = keyConverter.read(keyVectorDist.first.first, 0)
        return Pair(Pair(key, keyVectorDist.first.second), keyVectorDist.second)
    }

    internal fun addIfSmaller(keyVectorDists: MutableList<Pair<Pair<K, FloatVector>, Float>>, keyVectorDist: Pair<Pair<DirectBuffer, FloatVector>, Float>): Float {
        var maxKeyVectorIndex = 0

        for (i in 1 until keyVectorDists.size) {
            if (keyVectorDists[maxKeyVectorIndex].second < keyVectorDists[i].second) {
                maxKeyVectorIndex = i
            }
        }

        val maxTupleDist = keyVectorDists[maxKeyVectorIndex].second

        if (keyVectorDist.second < maxTupleDist) {
            keyVectorDists[maxKeyVectorIndex] = convertRawKeyVectorDist(keyVectorDist)
        }

        return maxTupleDist
    }

    fun getNeighbours(txn: Txn<DirectBuffer>, vector: FloatVector, numNeighbours: Int, distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer): Array<Pair<Pair<K, FloatVector>, Float>> {
        require(numNeighbours > 0) { "numNeighbours must be a value greater than 0" }

        val vectorDiffsList = mutableListOf<Pair<Pair<K, FloatVector>, Float>>()

        var maxDist: Float = -1f

        db.dbi.iterate(txn).use {
            for (keyVal in it) {
                val entryVector = keyVal.`val`().getFloatVector(0)
                val dist = distanceMeasurer.calcDistance(vector, entryVector)

                if (maxDist < 0 || dist < maxDist) {
                    val keyVector = Pair(Pair(keyVal.key(), entryVector), dist)

                    if (vectorDiffsList.size < numNeighbours) {
                        vectorDiffsList.add(convertRawKeyVectorDist(keyVector))
                    } else {
                        maxDist = addIfSmaller(vectorDiffsList, keyVector)
                    }
                }
            }
        }

        vectorDiffsList.sortBy { keyVectorDist -> keyVectorDist.second }

        return vectorDiffsList.toTypedArray()
    }
}