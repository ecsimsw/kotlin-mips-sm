package computer.architecture.cpu

import computer.architecture.memory.Memory

class ALU(
    private val memory: Memory,
    private val registers: Registers
) {

    fun process(executionInfo: ExecutionInfo) {
        val opcode = executionInfo.opcode
        val operand1 = executionInfo.operand1
        val operand2 = executionInfo.operand2

        if (opcode == Opcode.ADD) {
            registers.r[0] = operand1 + operand2
            return
        }

        if (opcode == Opcode.MINUS) {
            registers.r[0] = operand1 - operand2
            return
        }

        if (opcode == Opcode.MULTIPLY) {
            registers.r[0] = operand1 * operand2
            return
        }

        if (opcode == Opcode.DIVIDE) {
            registers.r[0] = operand1 / operand2
            return
        }

        if (opcode == Opcode.MOVE) {
            registers.r[operand1] = registers.r[operand2]
            return
        }

        if (opcode == Opcode.JUMP) {
            registers.pc = registers.r[operand1]
            return
        }

        if (opcode == Opcode.BRANCH) {
            registers
        }

        if(opcode == Opcode.HALT) {
            registers.pc = Int.MAX_VALUE
            return
        }

        if (opcode == Opcode.CONDITION) {
            if (operand1 >= operand2) {
                registers.r[0] = 0
                return
            }
            registers.r[0] = 1
            return
        }
    }
}
