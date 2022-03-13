package computer.architecture.cpu

import computer.architecture.memory.Memory
import computer.architecture.memory.Results

class ControlUnit(
    private val memory: Memory,
    private val results: Results,
) {
    private var registers = Registers(10)
    private val alu = ALU(registers)

    fun process() {
        while (registers.pc < memory.size()) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
            execute(executionInfo)
            store(instruction)
        }
    }

    private fun fetch(index: Int): String {
        val instruction = memory.readLine(index)
        registers.pc++
        return instruction
    }

    private fun decode(instruction: String): ExecutionInfo {
        try {
            val split = instruction.split(" ")
            return ExecutionInfo(Opcode.of(split[0]), split[1], split[2])
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("invalid instruction in : $instruction\n${e.message}")
        }
    }

    private fun execute(executionInfo: ExecutionInfo) {
        alu.process(executionInfo)
    }

    private fun store(instruction: String) {
        results.log(instruction, registers)
    }
}
