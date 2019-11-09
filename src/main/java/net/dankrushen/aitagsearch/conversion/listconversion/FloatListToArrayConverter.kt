package net.dankrushen.aitagsearch.conversion.listconversion

class FloatListToArrayConverter: ListToArrayConverter<Float> {
    companion object {
        val converter = FloatListToArrayConverter()
    }

    override fun listToArray(list: List<Float>): Array<Float> {
        return list.toTypedArray()
    }
}