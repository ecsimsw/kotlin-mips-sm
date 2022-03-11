package computer.architecture.cpu

import computer.architecture.memory.Memory

class ControlUnit(
    private val memory: Memory
) {
    private var registers: Registers = Registers()
    private val alu: ALU = ALU(registers)

    fun process() {
        while (registers.pc < memory.size()) {
            val instruction = fetch(registers.pc++)
            val executionInfo = decode(instruction)
            execute(executionInfo)

            println(instruction)
            registers.r.forEach { print("$it ") }
            println()
        }
    }

    private fun fetch(index: Int): String {
        return memory.readLine(index)
    }

    private fun execute(executionInfo: ExecutionInfo) {
        val opcode = executionInfo.opcode
        val operand = executionInfo.operand1

        if (opcode == Opcode.JUMP) {
            registers.pc = registers.r[operand]
            return
        }

        if (opcode == Opcode.BRANCH) {
            val instruction = fetch(operand)
            val newExecutionInfo = decode(instruction)
            execute(newExecutionInfo)
            return
        }

        if (opcode == Opcode.HALT) {
            registers.pc = Int.MAX_VALUE
            return
        }

        alu.process(executionInfo)
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
            val index = operand.substring(1).toInt()
            return registers.r[index]
        }
        return Integer.decode(operand)
    }
}
