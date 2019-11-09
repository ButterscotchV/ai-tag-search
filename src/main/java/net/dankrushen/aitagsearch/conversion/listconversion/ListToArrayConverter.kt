package net.dankrushen.aitagsearch.conversion.listconversion

interface ListToArrayConverter<T> {
    fun listToArray(list: List<T>): Array<T>
}