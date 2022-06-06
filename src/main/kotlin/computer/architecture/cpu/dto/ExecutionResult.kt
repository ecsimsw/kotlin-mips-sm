package computer.architecture.cpu.dto

import computer.architecture.cpu.ControlSignal

data class ExecutionResult(
    val valid: Boolean = false,
    val pc: Int = 0,
    val aluValue: Int = 0,
    val readData2: Int = 0,
    val writeReg: Int = 0,
    val nextPc: Int = 0,
    val jump: Boolean = false,
    val branch: Boolean = false,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val pn: Int = -1
)