package computer.architecture.cpu

import computer.architecture.cpu.register.ScoreBoardingRegisters

class DataDependencyUnit(
    private val scoreBoardingRegisters: ScoreBoardingRegisters
) {
    fun execute(nextIdEx: DecodeResult, nextIfId: FetchResult): DataDependencyResult {
        val hasHazard = hasHazard(nextIdEx.readReg1, nextIdEx.readReg2)
        if (hasHazard) {
            nextIfId.valid = false
            nextIdEx.valid = false
            nextIdEx.controlSignal = ControlSignal.NONE
        }
        scoreBoardingRegisters.book(nextIdEx.controlSignal.regWrite, nextIdEx.writeReg, nextIdEx.pc)
        return DataDependencyResult(hasHazard, nextIdEx.pc)
    }

    private fun hasHazard(readReg1: Int, readReg2: Int): Boolean {
        val valid1 = scoreBoardingRegisters.isValid(readReg1)
        val valid2 = scoreBoardingRegisters.isValid(readReg2)
        return !(valid1 && valid2)
    }
}

data class DataDependencyResult(
    val isHazard: Boolean,
    val freezePc: Int
)
