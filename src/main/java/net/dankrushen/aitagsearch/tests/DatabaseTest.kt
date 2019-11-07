package net.dankrushen.aitagsearch.tests

import net.dankrushen.aitagsearch.datatypes.FloatVector
import net.dankrushen.aitagsearch.database.DatabaseUtils
import net.dankrushen.aitagsearch.database.StringVectorDatabase
import net.dankrushen.aitagsearch.extensions.getFloatVector
import net.dankrushen.aitagsearch.extensions.toUnsafeBuffer
import org.agrona.DirectBuffer
import org.lmdbjava.Env
import java.io.File

fun main() {
    val env = DatabaseUtils.createEnv(File("C:\\Users\\Dankrushen\\Desktop\\NewDatabase"), 1073741824, 1)

    env.use {
        val db = StringVectorDatabase(env, "Test")

        db.use {
            testDatabase(env, db)
        }
    }
}

fun testDatabase(env: Env<DirectBuffer>, db: StringVectorDatabase) {
    val demoKeyVector = Pair("test", FloatVector.zeros(128))

    for (i in 0 until demoKeyVector.second.dimension) {
        demoKeyVector.second[i] = i
    }

    println("Original: ")
    println("\tKey: ${demoKeyVector.first}")
    println("\tVector: ${demoKeyVector.second}")
    println("\tEncoded Key: ${demoKeyVector.first.toUnsafeBuffer(env.maxKeySize).getStringUtf8(0)}")
    println("\tEncoded Vector: ${demoKeyVector.second.toUnsafeBuffer().getFloatVector(0)}")

    var testKeyVectorResult: Pair<String, FloatVector>? = null

    env.txnWrite().use {
        db.putKeyVector(it, demoKeyVector, commitTxn = true)
    }

    env.txnRead().use {
        testKeyVectorResult = db.getKeyVector(it, demoKeyVector.first)
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