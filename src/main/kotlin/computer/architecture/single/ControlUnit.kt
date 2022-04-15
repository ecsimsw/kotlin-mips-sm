package computer.architecture.single

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers = Registers(32),
    private val decodeUnit: DecodeUnit = DecodeUnit()
) {
    private val controlSignal = ControlSignal()
    private val alu = ALU()

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
//            val executeResult = execute(executionInfo)
//            val memoryAccessResult = memoryAccess(executeResult)
//            val processResult = writeBack(memoryAccessResult)
//            storeLog(processResult)
        }
    }

    private fun fetch(address: Int): Int {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction: Int): ExecutionInfo {
        val executionInfo = decodeUnit.decode(instruction)
        controlSignal.setSignals(executionInfo.opcode)
        return executionInfo
    }

    private fun execute(executionInfo: ExecutionInfo) {
        aluOperate(executionInfo)
        calculateAddress()
    }

    private fun readRegister(executionInfo: ExecutionInfo) {
        TODO("Not yet implemented")
    }

    private fun calculateAddress() {
        TODO("Not yet implemented")
    }

    private fun memoryAccess(executeResult: Unit): Any {
        TODO("Not yet implemented")
    }

    private fun aluOperate(executionInfo: ExecutionInfo) {
        TODO("Not yet implemented")
    }

    private fun memoryOperate(executionInfo: ExecutionInfo) {
        TODO("Not yet implemented")
    }

    private fun writeRegister(executionInfo: ExecutionInfo) {
        TODO("Not yet implemented")
    }

    private fun storeLog(processResult: Any) {
        TODO("Not yet implemented")
    }

    private fun writeBack(memoryAccessResult: Any) : Any {
        TODO("Not yet implemented")
    }

}
