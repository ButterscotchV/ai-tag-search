package net.dankrushen.aitagsearch.tests

import net.dankrushen.aitagsearch.comparison.CosineDistance
import net.dankrushen.aitagsearch.conversion.FloatVectorConverter
import net.dankrushen.aitagsearch.conversion.StringConverter
import net.dankrushen.aitagsearch.database.DatabaseUtils
import net.dankrushen.aitagsearch.database.TypedPairDatabase
import net.dankrushen.aitagsearch.database.query.NearestNeighbour
import net.dankrushen.aitagsearch.datatypes.FloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVector
import org.agrona.DirectBuffer
import org.lmdbjava.Env
import java.io.File

private fun <T> time(action: String, block: () -> T): Pair<T, Long> {
    println("$action...")

    val start = System.currentTimeMillis()
    val result = block.invoke()
    val end = System.currentTimeMillis()

    val time = end - start
    println("$action took $time ms")

    return Pair(result, time)
}

const val bytesInGb = 1073741824L

fun main() {
    val env = DatabaseUtils.createEnv(File("C:\\Users\\Dankrushen\\Desktop\\NewDatabase"), bytesInGb * 2, 1)

    env.use {
        val db = TypedPairDatabase(env, "Test", StringConverter.converter, FloatVectorConverter.converter)

        db.use {
            testDatabase(env, db)
            println()
            // testWritingToDatabase(env, db, 1600000)
            // println()
            testNearestNeighbour(env, db, 150)
        }
    }
}

fun testDatabase(env: Env<DirectBuffer>, db: TypedPairDatabase<String, FloatVector>) {
    val demoKeyVector = Pair("test", FloatVector.zeros(128))

    for (i in 0 until demoKeyVector.second.dimension) {
        demoKeyVector.second[i] = i
    }

    println("Original: ")
    println("\tKey: ${demoKeyVector.first}")
    println("\tVector: ${demoKeyVector.second}")
    println("\tEncoded Vector: ${FloatVectorConverter.converter.toDirectBuffer(demoKeyVector.second).getFloatVector(0)}")

    var testKeyVectorResult: Pair<String, FloatVector>? = null

    env.txnWrite().use {
        db.putPair(it, demoKeyVector, commitTxn = true)
    }

    env.txnRead().use {
        testKeyVectorResult = db.getPair(it, demoKeyVector.first)
    }

    if (testKeyVectorResult == null)
        throw Exception("KeyVector not found in database")

    println()
    println("Fetched: ")
    println("\tKey: ${testKeyVectorResult?.first ?: "null"}")
    println("\tVector: ${testKeyVectorResult?.second?.toString() ?: "null"}")

    println()
    println("Original and Fetched Equal: ${testKeyVectorResult?.second == demoKeyVector.second}")
}

fun testWritingToDatabase(env: Env<DirectBuffer>, db: TypedPairDatabase<String, FloatVector>, numVectors: Int) {
    time("Generating and inserting $numVectors random key vectors") {
        var txn = env.txnWrite()

        for (keyVectorNum in 0 until numVectors) {
            val keyVector = Pair("test${keyVectorNum}", FloatVector.zeros(128))

            for (i in 0 until keyVector.second.dimension) {
                keyVector.second[i] = Math.random()
            }

            db.putPair(txn, keyVector, commitTxn = false)

            if (keyVectorNum % 100 == 0) {
                txn.commit()
                txn.close()

                txn = env.txnWrite()
            }
        }
    }
}

fun testNearestNeighbour(env: Env<DirectBuffer>, db: TypedPairDatabase<String, FloatVector>, numNeighbours: Int, sampleCount: Int = 100) {
    var sumTime = 0L

    val testVector = FloatVector.ones(128)
    for (i in 0 until sampleCount) {
        val result = time("Fetching $numNeighbours nearest neighbours") {
            val txn = env.txnRead()

            val nearestNeighbour = NearestNeighbour(db, StringConverter.converter)
            val neighbours = nearestNeighbour.getNeighbours(txn, testVector, numNeighbours, CosineDistance.measurer)

            env.close()

            for (keyVectorDist in neighbours) {
                println(keyVectorDist)
            }
        }

        sumTime += result.second
    }

    println("Fetching $numNeighbours nearest neighbours took on average ${sumTime / sampleCount} ms over $sampleCount samples")
}