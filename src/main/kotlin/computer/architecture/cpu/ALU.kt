package computer.architecture.cpu

class ALU(
    private val registers: Registers
) {

    fun process(executionInfo: ExecutionInfo) {
        val opcode = executionInfo.opcode
        val operand1 = executionInfo.operand1
        val operand2 = executionInfo.operand2

        if (opcode == Opcode.HALT) {
            registers.pc = Int.MAX_VALUE
            return
        }

        if (opcode == Opcode.ADD) {
            registers.r[0] = value(operand1) + value(operand2)
        }

        if (opcode == Opcode.MINUS) {
            registers.r[0] = value(operand1) - value(operand2)
        }

        if (opcode == Opcode.MULTIPLY) {
            registers.r[0] = value(operand1) * value(operand2)
        }

        if (opcode == Opcode.DIVIDE) {
            registers.r[0] = value(operand1) / value(operand2)
        }

        if (opcode == Opcode.CONDITION) {
            registers.r[0] = value(operand1) < value(operand2)
        }

        if (opcode == Opcode.JUMP) {
            registers.pc = registers.r[value(operand1)]
        }

        if (opcode == Opcode.MOVE) {
            registers.r[registerNumber(operand1)] = value(operand2)
        }

        if (opcode == Opcode.BRANCH) {
            if (registers.r[0] == 1) {
                registers.pc = value(operand1)
            }
        }
    }

    private fun value(operand: String): Int {
        if (operand.startsWith("R")) {
            return registers.r[registerNumber(operand)]
        }
        return Integer.decode(operand)
    }

    private fun registerNumber(operand: String): Int {
        if (operand.startsWith("R")) {
            return operand.substring(1).toInt()
        }
        throw IllegalArgumentException("This is not the format of register operand")
    }
}
