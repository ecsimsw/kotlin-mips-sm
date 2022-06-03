package computer.architecture.cpu.bht

import kotlin.math.pow

class GlobalHistoryRegister(
    historyBitSize: Int = 4,
) : IHistoryRegister {

    private val maximumHistoryValue = 2.0.pow(historyBitSize).toInt()
    private var historyValue = 0

    override fun update(pc: Int, isTaken: Boolean) {
        var updateValue = historyValue shl 1
        if (isTaken) {
            updateValue += 1
        }
        if (updateValue >= maximumHistoryValue) {
            updateValue -= maximumHistoryValue
        }
        historyValue = updateValue
    }

    override fun valueOf(pc: Int): Int {
        return historyValue
    }
}
