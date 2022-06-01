package computer.architecture.cpu

data class FetchResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val instruction: Int = 0,
    val pn : Int = -1
)

data class DecodeResult(
    var valid: Boolean = false,
    val pc: Int = 0,
    val shiftAmt: Int = 0,
    val immediate: Int = 0,
    val address: Int = 0,
    val readReg1: Int = 0,
    val readReg2: Int = 0,
    var readData1: Int = 0,
    var readData2: Int = 0,
    val writeReg: Int = 0,
    val jump: Boolean = false,
    val nextPc: Int =0,
    var controlSignal: ControlSignal = ControlSignal.NONE,
    val pn : Int = -1
)

data class ExecutionResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val aluValue: Int = 0,
    val readData2: Int = 0,
    val writeReg: Int = 0,
    val nextPc: Int = 0,
    val jump: Boolean = false,
    val branch: Boolean = false,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val pn : Int = -1
)

data class MemoryAccessResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val regWriteValue: Int = 0,
    val writeReg: Int = 0,
    val address: Int =0, // TODO :: for logging
    val memReadValue: Int = 0,
    val memWriteValue: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val pn : Int = -1
)

data class WriteBackResult(
    val valid: Boolean = false,
    val pc: Int = 0, // TODO :: for logging
    val regWriteValue: Int = 0,
    val writeReg: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE,
    val pn : Int = -1
)

data class CycleResult(
    val nextPc: Int = 0,
    val valid: Boolean = false,
    val value: Int = 0,
    val isEnd: Boolean = false,
    val lastCycle: Boolean = false,
    val pn: Int = -1
)
