package computer.architecture.cpu

import computer.architecture.memory.Memory

class ControlUnit(
    private val memory: Memory,
) {
    private var registers: Registers = Registers()
    private val alu: ALU = ALU(memory, registers)

    fun process() {
        while (registers.pc < memory.size()) {
            val instruction = fetch()
            val executionInfo = decode(instruction)
            execute(executionInfo)
        }
    }

    private fun fetch(): String {
        return memory[registers.pc++]
    }

    private fun decode(instruction: String): ExecutionInfo {
        val split = instruction.split(" ")
        val opcode = Opcode.of(split[0])
        val operand1 = operandValue(split[1])
        val operand2 = operandValue(split[2])
        return ExecutionInfo(opcode, operand1, operand2)
    }

    private fun operandValue(operand: String): Int {
        if (operand.startsWith("R")) {
            val registerIndex = operand.substring(1).toInt()
            return registers.r[registerIndex]
        }
        return Integer.decode(operand)
    }

    private fun execute(executionInfo: ExecutionInfo) {
        alu.process(executionInfo)
    }
}
