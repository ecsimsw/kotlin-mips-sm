package computer.architecture.cpu

data class FetchResult(
    val pc: Int,
    val instruction: Int
)

data class DecodeResult(
    val shiftAmt: Int,
    val immediate: Int,
    val address: Int,
    val readData1: Int,
    val readData2: Int,
    val writeRegister: Int,
)

data class ExecutionResult(
    val aluValue: Int,
    val memWriteData: Int,
    val writeRegister: Int,
    val nextPc: Int
)

data class MemoryAccessResult(
    val readData: Int,
    val aluValue: Int,
    val writeRegister: Int,
    val nextPc: Int,
)

data class WriteBackResult(
    val regWrite: Boolean,
    val writeRegister: Int,
    val writeData: Int,
    val nextPc: Int
)
