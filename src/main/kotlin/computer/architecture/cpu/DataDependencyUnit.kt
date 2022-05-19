package computer.architecture.cpu

import computer.architecture.component.Registers

class DataDependencyUnit(
    private val registers: Registers
) {
    fun hasHazard(readReg1: Int, readReg2: Int): Boolean{
        val valid1 = registers.isValid(readReg1)
        val valid2 = registers.isValid(readReg2)
        return !(valid1 && valid2)
    }
}
