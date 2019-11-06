package net.dankrushen.aitagsearch.tests

import net.dankrushen.aitagsearch.database.DatabaseUtils
import net.dankrushen.aitagsearch.database.KeyVectorDatabase
import net.dankrushen.aitagsearch.extensions.toFloatVector
import net.dankrushen.aitagsearch.extensions.toUTF8String
import net.dankrushen.aitagsearch.extensions.toUnsafeBuffer
import net.dankrushen.aitagsearch.types.FloatVector
import net.dankrushen.aitagsearch.types.KeyVector
import java.io.File

fun main() {
    val env = DatabaseUtils.createEnv(File("C:\\Users\\Dankrushen\\Desktop\\DatabaseTest"), 1073741824, 1)

    env.use {
        val db = KeyVectorDatabase(env, "Test")

        db.use {
            val demoKeyVector = KeyVector("test", FloatVector.zeros(128))

            for (i in 0 until demoKeyVector.vector.dimension) {
                demoKeyVector.vector[i] = i
            }

            println("Original: ")
            println("\tKey: ${demoKeyVector.key}")
            println("\tVector: ${demoKeyVector.vector}")
            println("\tEncoded Key: ${demoKeyVector.key.toUnsafeBuffer(env.maxKeySize).toUTF8String()}")
            println("\tEncoded Vector: ${demoKeyVector.vector.toUnsafeBuffer().toFloatVector()}")

            var testKeyVectorResult: KeyVector? = null

            env.txnWrite().use {
                db.putKeyVector(it, demoKeyVector, commitTxn = true)
            }

            env.txnRead().use {
                testKeyVectorResult = db.getKeyVector(it, demoKeyVector.key)
            }

            if (testKeyVectorResult == null)
                throw Exception("KeyVector not found in database")

            println()
            println("Fetched: ")
            println("\tKey: ${testKeyVectorResult?.key ?: "null"}")
            println("\tVector: ${testKeyVectorResult?.vector?.toString() ?: "null"}")

            println()
            println("Original and Fetched Equal: ${testKeyVectorResult?.vector == demoKeyVector.vector}")
        }
    }
}