package computer.architecture.cpu.bht

import kotlin.math.pow

class LocalHistoryTable(
    size : Int,
    historyBitSize : Int = 4,
) {
    private val maximumHistoryValue = 2.0.pow(historyBitSize).toInt()
    private val historyTable = mutableMapOf<Int, Int>()

    init {
        for(i in 0 .. size) {
            historyTable[i] = 0
        }
    }

    fun update(key: Int, isTaken: Boolean) {
        historyTable[key] = historyTable[key]!! * 2
        if(isTaken) {
            historyTable[key] = historyTable[key]!! + 1
        }

        if(historyTable[key]!! >= maximumHistoryValue) {
            historyTable[key] = historyTable[key]!! - maximumHistoryValue
        }
    }

    fun historyValue(key : Int) : Int {
        return historyTable[key]!!
    }
}
