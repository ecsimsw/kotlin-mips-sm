package computer.architecture.single

class ControlUnit(
    private val memory: MemoryBinary,
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            decode(instruction)
        }
    }

    private fun fetch(address: Int): String {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction : String) {
        decodeUnit.decode(instruction)
    }
}
