package computer.architecture.cpu.dto

import computer.architecture.cpu.ControlSignal

data class MemoryAccessResult(
    val valid: Boolean = false,
    val pc: Int = 0,
    val regWriteValue: Int = 0,
    val writeReg: Int = 0,
    val address: Int = 0,
    val memReadValue: Int = 0,
    val memWriteValue: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val pn: Int = -1
)