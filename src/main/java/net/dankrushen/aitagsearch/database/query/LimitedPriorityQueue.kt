package net.dankrushen.aitagsearch.database.query

import java.util.*

/**
 * A queue that has limited capacity. Once it hits the maximum defined capacity
 * it will drop off the one with the most cost.
 *
 * @author thomas.jungblut
 */
class LimitedPriorityQueue<T>(private val maxCapacity: Int) {

    val queue: PriorityQueue<Entry<T>> = PriorityQueue(maxCapacity)

    val maximumPriority: Float
        get() {
            val p = queue.peek()
            return p?.value ?: Float.POSITIVE_INFINITY
        }

    val size: Int
        get() = queue.size

    val isEmpty: Boolean
        get() = size <= 0

    val isFull: Boolean
        get() = size >= maxCapacity

    data class Entry<T>(val data: T, val value: Float) : Comparable<Entry<T>> {

        override fun compareTo(t: Entry<T>): Int = t.value.compareTo(this.value)

        override fun toString(): String = data.toString()
    }

    fun add(element: T, cost: Float): Boolean {
        if (isFull) {
            if (cost > maximumPriority) {
                return false
            }

            queue.add(Entry(element, cost))
            queue.poll()
        } else {
            queue.add(Entry(element, cost))
        }
        return true
    }

    fun peek(): T? {
        val p = queue.peek()
        return p?.data
    }

    fun poll(): T? {
        val p = queue.poll()
        return p?.data
    }

    fun toList(): List<T> {
        val list = mutableListOf<T>()
        for (entry in queue) {
            list.add(entry.data)
        }
        return list
    }

    override fun toString(): String {
        return queue.toString()
    }
}