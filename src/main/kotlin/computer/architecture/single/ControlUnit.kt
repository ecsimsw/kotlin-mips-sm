package computer.architecture.single

class ControlUnit(
    private val memory: MemoryBinary,
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val controlSignal = ControlSignal()

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
            println(executionInfo.opcode)
            readRegister(executionInfo)
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

    private fun readRegister(executionInfo : ExecutionInfo) {

    }
}
