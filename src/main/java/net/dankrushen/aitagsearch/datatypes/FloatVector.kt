package net.dankrushen.aitagsearch.datatypes

import net.dankrushen.aitagsearch.extensions.getFloatVector
import net.dankrushen.aitagsearch.extensions.getFloatVectorWithoutLength
import net.dankrushen.aitagsearch.extensions.putFloatVector
import net.dankrushen.aitagsearch.extensions.putFloatVectorWithoutLength
import org.agrona.concurrent.UnsafeBuffer
import java.nio.ByteBuffer
import kotlin.math.sqrt

data class FloatVector(val dims: FloatArray) : Cloneable {
    companion object {
        fun valueFloatArray(value: Float, count: Int): FloatArray {
            val vals = FloatArray(count)

            for (i in 0 until count) {
                vals[i] = value
            }

            return vals
        }

        fun valueFloatVector(value: Float, dimension: Int): FloatVector {
            return FloatVector(valueFloatArray(value, dimension))
        }

        fun zeros(dimension: Int): FloatVector = FloatVector(FloatArray(dimension))
        fun ones(dimension: Int): FloatVector = valueFloatVector(1f, dimension)

        fun toUnsafeBuffer(vector: FloatVector): UnsafeBuffer {
            val byteBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(vector.sizeBytes))

            byteBuffer.putFloatVector(0, vector)

            return byteBuffer
        }

        fun toUnsafeBufferWithoutLength(vector: FloatVector): UnsafeBuffer {
            val byteBuffer = UnsafeBuffer(ByteBuffer.allocateDirect(vector.sizeBytesWithoutLength))

            byteBuffer.putFloatVectorWithoutLength(0, vector)

            return byteBuffer
        }

        fun toByteArrayWithoutLength(vector: FloatVector): ByteArray {
            return toUnsafeBufferWithoutLength(vector).byteArray()
        }

        fun toByteArray(vector: FloatVector): ByteArray {
            return toUnsafeBuffer(vector).byteArray()
        }

        fun fromByteArray(byteArray: ByteArray): FloatVector {
            return UnsafeBuffer(byteArray).getFloatVector(0)
        }

        fun fromByteArrayWithoutLength(byteArray: ByteArray, length: Int): FloatVector {
            return UnsafeBuffer(byteArray).getFloatVectorWithoutLength(0, length)
        }

        fun toString(vector: FloatVector): String {
            return vector.dims.joinToString(" ")
        }

        fun fromString(string: String): FloatVector {
            val dimParts = string.split(' ')

            val dimensionVals = mutableListOf<Float>()

            for (dim in dimParts) {
                dimensionVals.add(dim.toFloatOrNull() ?: continue)
            }

            return FloatVector(dimensionVals.toFloatArray())
        }
    }

    constructor(dimension: Int) : this(FloatArray(dimension))

    // One int for the dimension and one int of bytes per dimension
    val sizeBytes: Int
        get() = Int.SIZE_BYTES + (dimension * Int.SIZE_BYTES)

    val sizeBytesWithoutLength: Int
        get() = dimension * Int.SIZE_BYTES

    val dimension: Int
        get() = dims.size

    val sqrMagnitude: Float
        get() {
            var sqrMagnitude = 0f

            for (dimVal in dims) {
                sqrMagnitude += dimVal * dimVal
            }

            return sqrMagnitude
        }

    val magnitude: Float
        get() = sqrt(sqrMagnitude)

    val normalized: FloatVector
        get() = this / magnitude

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FloatVector

        if (!dims.contentEquals(other.dims)) return false

        return true
    }

    override fun hashCode(): Int {
        return dims.contentHashCode()
    }

    fun toUnsafeBuffer(): UnsafeBuffer = toUnsafeBuffer(this)
    fun toUnsafeBufferWithoutLength(): UnsafeBuffer = toUnsafeBufferWithoutLength(this)
    fun toByteArray(): ByteArray = toByteArray(this)
    fun toByteArrayWithoutLength(): ByteArray = toByteArrayWithoutLength(this)
    override fun toString(): String = toString(this)

    operator fun get(dimension: Int) = dims[dimension]
    operator fun set(dimension: Int, value: Number) {
        dims[dimension] = value.toFloat()
    }

    override fun clone(): FloatVector = FloatVector(dims.clone())

    private fun checkDimension(vector: FloatVector) {
        require(dimension == vector.dimension) { "\"vector\" must be of the same dimension as the vector being operated on" }
    }

    fun distanceTo(vector: FloatVector): Float = (vector - this).magnitude

    fun dot(vector: FloatVector): Float {
        checkDimension(vector)

        var dotProduct = 0f

        for (i in 0 until dimension) {
            dotProduct += this[i] * vector[i]
        }

        return dotProduct
    }

    operator fun plus(vector: FloatVector): FloatVector {
        checkDimension(vector)

        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] + vector[i]
        }

        return newVector
    }

    operator fun minus(vector: FloatVector): FloatVector {
        checkDimension(vector)

        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] - vector[i]
        }

        return newVector
    }

    operator fun plusAssign(vector: FloatVector) {
        checkDimension(vector)

        for (i in 0 until dimension) {
            this[i] += vector[i]
        }
    }

    operator fun minusAssign(vector: FloatVector) {
        checkDimension(vector)

        for (i in 0 until dimension) {
            this[i] -= vector[i]
        }
    }

    operator fun times(value: Float): FloatVector {
        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] * value
        }

        return newVector
    }

    operator fun div(value: Float): FloatVector {
        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] / value
        }

        return newVector
    }

    operator fun timesAssign(value: Float) {
        for (i in 0 until dimension) {
            this[i] *= value
        }
    }

    operator fun divAssign(value: Float) {
        for (i in 0 until dimension) {
            this[i] /= value
        }
    }
}