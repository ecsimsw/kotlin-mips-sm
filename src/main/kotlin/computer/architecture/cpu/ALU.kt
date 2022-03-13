package computer.architecture.cpu

class ALU(
    private val registers: Registers
) {
    private val operations: MutableMap<Opcode, (Operand, Operand) -> Unit> = mutableMapOf()

    init {
        operations[Opcode.ADD] = { op1, op2 -> registers.r[0] = value(op1) + value(op2) }
        operations[Opcode.MINUS] = { op1, op2 -> registers.r[0] = value(op1) - value(op2) }
        operations[Opcode.MULTIPLY] = { op1, op2 -> registers.r[0] = value(op1) * value(op2) }
        operations[Opcode.DIVIDE] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
//        operations[Opcode.SLL] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
//        operations[Opcode.SRL] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
//        operations[Opcode.AND] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
//        operations[Opcode.OR] = { op1, op2 -> registers.r[0] = value(op1) / value(op2) }
        operations[Opcode.CONDITION] = { op1, op2 -> registers.r[0] = value(op1) < value(op2) }
        operations[Opcode.MOVE] = { op1, op2 -> registers.r[op1.registerNumber()] = value(op2) }
        operations[Opcode.JUMP] = { op1, op2 -> registers.pc = registers.r[value(op1)] }
        operations[Opcode.BRANCH] = { op1, op2 -> if (registers.r[0] == 1) registers.pc = value(op1) }
        operations[Opcode.HALT] = { op1, op2 -> registers.pc = Int.MAX_VALUE }
    }

    fun process(executionInfo: ExecutionInfo) {
        process(executionInfo.opcode, executionInfo.operand1, executionInfo.operand2)
    }

    fun process(opcode: Opcode, operand1: Operand, operand2: Operand) {
        val operation = operations[opcode] ?: throw IllegalArgumentException("Opcodes that cannot be computed")
        operation.invoke(operand1, operand2)
    }

    private fun value(operand: Operand): Int {
        if (operand.isRegisterNumber()) {
            return registers.r[operand.registerNumber()]
        }
        if (operand.isHexadecimalNumber()) {
            return operand.getIntValue()
        }
        throw IllegalArgumentException("This is not the format of operand")
    }
}
