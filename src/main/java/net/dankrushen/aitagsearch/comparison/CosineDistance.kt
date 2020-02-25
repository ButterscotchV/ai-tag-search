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

package net.dankrushen.aitagsearch.comparison

import net.dankrushen.aitagsearch.datatypes.FloatVector

class CosineDistance : DistanceMeasurer {
    companion object {
        val measurer = CosineDistance()
    }

    override fun calcDistance(firstVector: FloatVector, secondVector: FloatVector): Float {
        val dotProduct = firstVector.dot(secondVector)
        var denominator = firstVector.magnitude * secondVector.magnitude

        // Correct for float rounding errors
        if (denominator < dotProduct) {
            return if (dotProduct == 0f) 1f else 0f
        }

        return if (denominator == 0f) 1f else 1f - (dotProduct / denominator)
    }
}