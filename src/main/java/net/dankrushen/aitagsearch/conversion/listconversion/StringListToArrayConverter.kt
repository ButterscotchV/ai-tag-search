package net.dankrushen.aitagsearch.conversion.listconversion

class StringListToArrayConverter : ListToArrayConverter<String> {
    companion object {
        val converter = StringListToArrayConverter()
    }

    override fun listToArray(list: List<String>): Array<String> {
        return list.toTypedArray()
    }
}