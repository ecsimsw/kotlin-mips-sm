package computer.architecture.cpu

class ALU(
    private val registers: Registers
) {
    private val operations: MutableMap<Opcode, (String, String) -> Unit> = mutableMapOf()

    init {
        operations[Opcode.ADD] = { op1, op2 -> registers.r[0] = value(op1) + value(op2) }
        operations[Opcode.MINUS] = { op1, op2 -> registers.r[0] = value(op1) - value(op2) }
        operations[Opcode.MULTIPLY] = { op1, op2 -> registers.r[0] = value(op1) * value(op2) }
        operations[Opcode.DIVIDE] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
        operations[Opcode.CONDITION] = { op1, op2 -> registers.r[0] = value(op1) < value(op2) }
        operations[Opcode.MOVE] = { op1, op2 -> registers.r[registerNumber(op1)] = value(op2) }
        operations[Opcode.JUMP] = { op1, op2 -> registers.pc = registers.r[value(op1)] }
        operations[Opcode.BRANCH] = { op1, op2 -> if (registers.r[0] == 1) registers.pc = value(op1) }
        operations[Opcode.HALT] = { op1, op2 -> registers.pc = Int.MAX_VALUE }
    }

    fun process(executionInfo: ExecutionInfo) {
        val opcode = executionInfo.opcode
        val operation = operations[opcode] ?: throw IllegalArgumentException("Opcodes that cannot be computed")
        operation.invoke(executionInfo.operand1, executionInfo.operand2)
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
