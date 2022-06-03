package computer.architecture.cpu.bht

import computer.architecture.cpu.prediction.IBitStateMachine
import computer.architecture.cpu.prediction.SaturationTwoBitStateMachine

class PatternHistoryRegister(
    size: Int
) {
    private val histories = Array<IBitStateMachine>(size) { SaturationTwoBitStateMachine() }

    fun pattern(key: Int): IBitStateMachine {
        return histories[key]
    }

    fun update(key: Int, isTaken: Boolean) {
        return histories[key].update(isTaken)
    }
}
