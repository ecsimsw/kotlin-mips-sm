package computer.architecture.cpu

import computer.architecture.memory.Memory
import computer.architecture.memory.Results

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers,
    private val results: Results
) {
    private val alu = ALU(registers, memory)
    private val decodeUnit = DecodeUnit()

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
            execute(executionInfo)
            store(instruction)
        }
    }

    private fun fetch(address: Int): String {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction: String): ExecutionInfo {
        return decodeUnit.decode(instruction)
    }

    private fun execute(executionInfo: ExecutionInfo) {
        alu.process(executionInfo)
    }

    private fun store(instruction: String) {
        results.saveLog(instruction, registers)
    }
}
