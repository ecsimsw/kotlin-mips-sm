package computer.architecture.cpu

import computer.architecture.cpu.register.ScoreBoardingRegisters

class DataDependencyUnit(
    private val scoreBoardingRegisters: ScoreBoardingRegisters
) {
    fun hasHazard(readReg1: Int, readReg2: Int): Boolean{
        val valid1 = scoreBoardingRegisters.isValid(readReg1)
        val valid2 = scoreBoardingRegisters.isValid(readReg2)
        return !(valid1 && valid2)
    }
}
