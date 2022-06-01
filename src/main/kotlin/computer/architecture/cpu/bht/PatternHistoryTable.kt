package computer.architecture.cpu.bht

import computer.architecture.cpu.prediction.IBitStateMachine
import computer.architecture.cpu.prediction.SaturationTwoBitStateMachine

class PatternHistoryTable(
    size: Int
) {
    private val historyTable = mutableMapOf<Int, IBitStateMachine>()

    init {
        for(i in 0 .. size) {
            historyTable[i] = SaturationTwoBitStateMachine()
        }
    }

    fun pattern(key : Int) : IBitStateMachine {
        return historyTable[key]!!
    }

    fun update(key : Int, isTaken: Boolean) {
        historyTable[key]!!.change(isTaken)
    }
}
