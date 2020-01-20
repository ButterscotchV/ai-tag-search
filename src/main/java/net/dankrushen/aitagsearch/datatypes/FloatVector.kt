package net.dankrushen.aitagsearch.datatypes

import kotlin.math.sqrt

data class FloatVector(val dims: FloatArray) : Cloneable {
    companion object {
        fun valueFloatArray(value: Number, count: Int): FloatArray {
            val vals = FloatArray(count)

            for (i in 0 until count) {
                vals[i] = value.toFloat()
            }

            return vals
        }

        fun valueFloatVector(value: Number, dimension: Int): FloatVector {
            return FloatVector(valueFloatArray(value, dimension))
        }

        fun zeros(dimension: Int): FloatVector = FloatVector(FloatArray(dimension))
        fun ones(dimension: Int): FloatVector = valueFloatVector(1f, dimension)

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

    val sizeBytes: Int
        get() = dimension * Int.SIZE_BYTES

    val dimension: Int
        get() = dims.size

    val sum: Float
        get() {
            var sum = 0f

            for (dimVal in dims) {
                sum += dimVal
            }

            return sum
        }

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

    override fun toString(): String = toString(this)

    operator fun get(dimension: Int) = dims[dimension]
    operator fun set(dimension: Int, value: Number) {
        dims[dimension] = value.toFloat()
    }

    override fun clone(): FloatVector = FloatVector(dims.clone())

    fun checkDimension(vector: FloatVector) {
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

    operator fun unaryMinus(): FloatVector {
        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = -this[i]
        }

        return newVector
    }

    operator fun times(value: Number): FloatVector {
        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] * value.toFloat()
        }

        return newVector
    }

    operator fun div(value: Number): FloatVector {
        val newVector = FloatVector(dimension)
        for (i in 0 until dimension) {
            newVector[i] = this[i] / value.toFloat()
        }

        return newVector
    }

    operator fun timesAssign(value: Number) {
        for (i in 0 until dimension) {
            this[i] *= value.toFloat()
        }
    }

    operator fun divAssign(value: Number) {
        for (i in 0 until dimension) {
            this[i] /= value.toFloat()
        }
    }
}