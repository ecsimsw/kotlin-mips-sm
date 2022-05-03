package computer.architecture.cpu

data class FetchResult(
    val cycle:Int,
    val pc: Int = 0,
    val instruction: Int =0
)

data class DecodeResult(
    val shiftAmt: Int = 0,
    val immediate: Int = 0,
    val address: Int = 0,
    val readData1: Int = 0,
    val readData2: Int = 0,
    val writeRegister: Int = 0,
    val controlSignal: ControlSignal = ControlSignal()
)

data class ExecutionResult(
    val aluValue: Int = 0,
    val memWriteData: Int = 0,
    val writeRegister: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal()
)

data class MemoryAccessResult(
    val readData: Int = 0,
    val writeData: Int =0,
    val aluValue: Int = 0,
    val writeRegister: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal()
)

data class WriteBackResult(
    val regWrite: Boolean = false,
    val writeRegister: Int = 0,
    val writeData: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal()
)
