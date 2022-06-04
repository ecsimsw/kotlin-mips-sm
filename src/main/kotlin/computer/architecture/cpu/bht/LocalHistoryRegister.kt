package computer.architecture.cpu.bht

import kotlin.math.pow

class LocalHistoryRegister(
    val size: Int = 16,
    historyBitSize: Int = 4,
) : IHistoryRegister {

    private val maximumHistoryValue = 2.0.pow(historyBitSize).toInt()
    private var historyValue = Array(size) { 0 }

    override fun update(pc: Int, isTaken: Boolean) {
        val index = index(pc)
        var updateValue = historyValue[index] shl 1
        if (isTaken) {
            updateValue ++
        }
        if (updateValue >= maximumHistoryValue) {
            updateValue -= maximumHistoryValue
        }
        historyValue[index] = updateValue
    }

    override fun valueOf(pc: Int): Int {
        return historyValue[index(pc)]
    }

    private fun index(pc: Int): Int {
        return (pc / 4) % size
    }
}
