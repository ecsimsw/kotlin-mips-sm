package computer.architecture.cpu

data class FetchResult(
    val valid : Boolean = false,
    val pc: Int = 0,
    val instruction: Int =0
)

data class DecodeResult(
    val valid : Boolean = false,
    val pc: Int =0 ,
    val shiftAmt: Int = 0,
    val immediate: Int = 0,
    val address: Int = 0,
    val readData1: Int = 0,
    val readData2: Int = 0,
    val writeRegister: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class ExecutionResult(
    val valid : Boolean = false,
    val pc: Int =0 ,
    val aluValue: Int = 0,
    val memWriteValue: Int = 0,
    val writeRegister: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class MemoryAccessResult(
    val valid : Boolean = false,
    val pc: Int = 0 ,
    val memReadValue: Int = 0,
    val memWriteValue: Int =0,
    val aluValue: Int = 0,
    val writeRegister: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)

data class WriteBackResult(
    val valid : Boolean = false,
    val pc: Int = 0,
    val writeRegister: Int = 0,
    val regWriteValue: Int = 0,
    val nextPc: Int = 0,
    val controlSignal: ControlSignal = ControlSignal.NONE
)
