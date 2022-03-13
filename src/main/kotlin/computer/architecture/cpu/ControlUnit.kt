package computer.architecture.cpu

import computer.architecture.memory.Memory

class ControlUnit(
    private val memory: Memory
) {
    private var registers: Registers = Registers()
    private val alu: ALU = ALU(registers)

    fun process() {
        while (registers.pc < memory.size()) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
            execute(executionInfo)

            println(instruction)
            registers.r.forEach { print("$it ") }
            println()
        }
    }

    private fun fetch(index: Int): String {
        val instruction = memory.readLine(index)
        registers.pc++
        return instruction
    }

    private fun execute(executionInfo: ExecutionInfo) {
        alu.process(executionInfo)
    }

    private fun decode(instruction: String): ExecutionInfo {
        try {
            val split = instruction.split(" ")
            return ExecutionInfo(Opcode.of(split[0]), split[1], split[2])
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("invalid instruction : ${e.message}")
        }
    }
}
