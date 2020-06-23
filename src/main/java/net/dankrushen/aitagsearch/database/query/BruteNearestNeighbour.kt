package net.dankrushen.aitagsearch.database.query

import net.dankrushen.aitagsearch.comparison.DistanceMeasurer
import net.dankrushen.aitagsearch.comparison.EuclidianDistance
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.datatypes.FloatVector
import org.agrona.DirectBuffer
import org.lmdbjava.Txn

// Fake constructor to automatically provide a base array
@Suppress("FunctionName")
inline fun <reified K> BruteNearestNeighbour(db: TypedPairDatabase<K, FloatVector>, distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer) = BruteNearestNeighbour(db, distanceMeasurer, emptyArray())

class BruteNearestNeighbour<K>(val db: TypedPairDatabase<K, FloatVector>, val distanceMeasurer: DistanceMeasurer = EuclidianDistance.measurer, private val baseArray: Array<K>) {

    private fun addIfSmaller(values: Array<K?>, vectors: Array<FloatVector?>, distances: FloatArray, rawKey: DirectBuffer, vector: FloatVector, dist: Float, condition: ((key: K) -> Boolean)?): Float? {
        var maxIndex = 0
        var maxDist: Float? = null

        for (i in values.indices) {
            if (values[i] == null) {
                maxIndex = i
                maxDist = null
                break
            } else if (distances[maxIndex] < distances[i]) {
                maxIndex = i
                maxDist = distances[i]
            }
        }

        if (maxDist == null || dist < maxDist) {
            val key = db.keyConverter.read(rawKey, 0)

            // If the condition is either null or true (not false)
            if (condition?.invoke(key) != false) {
                values[maxIndex] = key
                vectors[maxIndex] = vector
                distances[maxIndex] = dist
            }
        }

        return maxDist
    }

    fun getNeighbours(txn: Txn<DirectBuffer>, vector: FloatVector, numNeighbours: Int, condition: ((key: K) -> Boolean)? = null, maxDist: Float? = null, minDist: Float? = null, sorted: Boolean = true): Array<Pair<Pair<K, FloatVector>, Float>?> {
        require(numNeighbours > 0) { "\"numNeighbours\" must be a value greater than 0" }

        val values = baseArray.copyOf(numNeighbours)
        val vectors = arrayOfNulls<FloatVector>(numNeighbours)
        val distances = FloatArray(numNeighbours)

        var internalMaxDist: Float? = maxDist
        db.dbi.iterate(txn).use {
            if (db.valueLength != null) {
                for (keyVal in it) {
                    val entryVector = db.valueConverter.readWithoutLength(keyVal.`val`(), db.valueIndex, db.valueLength)
                    val dist = distanceMeasurer.calcDistance(vector, entryVector)

                    if ((internalMaxDist == null || dist < internalMaxDist!!) && (minDist == null || dist > minDist)) {
                        val newMaxDist = addIfSmaller(values, vectors, distances, keyVal.key(), entryVector, dist, condition)

                        if (newMaxDist != null)
                            internalMaxDist = newMaxDist
                    }
                }
            } else {
                for (keyVal in it) {
                    val entryVector = db.valueConverter.read(keyVal.`val`(), db.valueIndex)
                    val dist = distanceMeasurer.calcDistance(vector, entryVector)

                    if ((internalMaxDist == null || dist < internalMaxDist!!) && (minDist == null || dist > minDist)) {
                        val newMaxDist = addIfSmaller(values, vectors, distances, keyVal.key(), entryVector, dist, condition)

                        if (newMaxDist != null)
                            internalMaxDist = newMaxDist
                    }
                }
            }
        }

        val vectorDiffs = arrayOfNulls<Pair<Pair<K, FloatVector>, Float>>(numNeighbours)

        for (i in values.indices) {
            if (values[i] == null)
                continue

            vectorDiffs[i] = Pair(Pair(values[i]!!, vectors[i]!!), distances[i])
        }

        if (sorted)
            vectorDiffs.sortBy { keyVectorDist -> keyVectorDist?.second }

        return vectorDiffs
    }
}