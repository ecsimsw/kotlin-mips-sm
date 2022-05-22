package computer.architecture.cpu

data class FetchResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val instruction: Int = 0
)

data class DecodeResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val dataHazard: Boolean = false,
    val shiftAmt: Int = 0,
    val immediate: Int = 0,
    val address: Int = 0,
    val readData1: Int = 0,
    val readData2: Int = 0,
    val writeRegister: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class ExecutionResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val aluValue: Int = 0,
    val memWriteValue: Int = 0,
    val writeRegister: Int = 0,
    val nextPc: Int = 0,
    val jump: Boolean = false,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class MemoryAccessResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val memReadValue: Int = 0,
    val memWriteValue: Int = 0,
    val aluValue: Int = 0,
    val writeRegister: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class WriteBackResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val writeRegister: Int = 0,
    val regWriteValue: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class CycleResult(
    val nextPc: Int = 0,
    val valid: Boolean = false,
    val value: Int = 0,
    val lastInstruction: Boolean = false,
    val lastCycle: Boolean = false
)
