/*
 * Copyright 2016 Thomas Jungblut
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dankrushen.aitagsearch.database.query.kdtree

import net.dankrushen.aitagsearch.datatypes.FloatVector

data class HyperRectangle(var min: FloatVector, var max: FloatVector) : Cloneable {

    companion object {
        fun infiniteHyperRectangle(dimension: Int): HyperRectangle {
            val min = FloatVector(dimension)
            val max = FloatVector(dimension)

            for (i in 0 until dimension) {
                min[i] = Float.NEGATIVE_INFINITY
                max[i] = Float.POSITIVE_INFINITY
            }
            return HyperRectangle(min, max)
        }
    }

    public override fun clone(): HyperRectangle = HyperRectangle(min.clone(), max.clone())

    fun closestPoint(t: FloatVector): FloatVector {
        val p = FloatVector(t.dimension)

        for (i in 0 until t.dimension) {
            when {
                t[i] <= min[i] -> {
                    p[i] = min[i]
                }

                t[i] >= max[i] -> {
                    p[i] = max[i]
                }

                else -> {
                    p[i] = t[i]
                }
            }
        }

        return p
    }
}