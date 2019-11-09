package net.dankrushen.aitagsearch.conversion.listconversion

class IntListToArrayConverter : ListToArrayConverter<Int> {
    companion object {
        val converter = IntListToArrayConverter()
    }

    override fun listToArray(list: List<Int>): Array<Int> {
        return list.toTypedArray()
    }
}