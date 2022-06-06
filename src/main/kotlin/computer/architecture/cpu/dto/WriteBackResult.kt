package computer.architecture.cpu.dto

import computer.architecture.cpu.ControlSignal

data class WriteBackResult(
    val valid: Boolean = false,
    val pc: Int = 0,
    val regWriteValue: Int = 0,
    val writeReg: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val nextPc: Int = 0,
    val pn: Int = -1
)