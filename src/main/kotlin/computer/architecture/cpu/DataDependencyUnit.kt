package computer.architecture.cpu

import computer.architecture.component.Registers

class DataDependencyUnit(
    private val registers: Registers
) {
    fun check(pc: Int, readReg1: Int, readReg2: Int): DataDependencyResult {
        val valid1 = registers.isValid(readReg1)
        val valid2 = registers.isValid(readReg2)
        return DataDependencyResult(valid1 and valid2, pc, 2)
    }
}

data class DataDependencyResult(
    val valid : Boolean,
    val freezePc : Int,
    val stallingCount : Int
)
