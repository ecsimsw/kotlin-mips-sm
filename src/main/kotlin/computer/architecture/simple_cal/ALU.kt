package computer.architecture.simple_cal

class ALU(
    private val registers: Registers,
    private val memory: Memory
) {
    private val operations: MutableMap<Opcode, (Int, Int) -> Unit> = mutableMapOf()

    init {
        val R :Array<Int> = registers.r

        operations[Opcode.ADD] = { op1, op2 -> R[0] = op1 + op2 }
        operations[Opcode.MINUS] = { op1, op2 -> R[0] = op1 - op2 }
        operations[Opcode.MULTIPLY] = { op1, op2 -> R[0] = op1 * op2 }
        operations[Opcode.DIVIDE] = { op1, op2 -> R[0] = op1 / op2 }
        operations[Opcode.MOD] = { op1, op2 -> R[0] = op1 % op2 }
        operations[Opcode.SLL] = { op1, op2 -> R[0] = op1 shl op2 }
        operations[Opcode.SRL] = { op1, op2 -> R[0] = op1 shr op2 }
        operations[Opcode.AND] = { op1, op2 -> R[0] = op1 and op2 }
        operations[Opcode.OR] = { op1, op2 -> R[0] = op1 or op2 }
        operations[Opcode.JUMP] = { op1, _ -> registers.pc = op1 }
        operations[Opcode.JUMP_AND_LINK] = { r1, address -> R[r1] = registers.pc; registers.pc = address }
        operations[Opcode.BRANCH] = { op1, _ -> if (R[0] == 1) registers.pc = op1 }
        operations[Opcode.BRANCH_ON_EQUAL] = { op1, op2 -> if (R[0] == op1) registers.pc = op2 }
        operations[Opcode.BRANCH_ON_NOT_EQUAL] = { op1, op2 -> if (R[0] != op1) registers.pc = op2 }
        operations[Opcode.CONDITION] = { op1, op2 -> R[0] = op1 < op2 }
        operations[Opcode.MOVE] = { r1, value -> R[r1] = value }
        operations[Opcode.LOAD_WORD] = { op1, _ -> R[0] = memory[op1] }
        operations[Opcode.STORE_WORD] = { op1, op2 -> memory[op2] = op1 }
        operations[Opcode.ANNOTATION] = { _, _ -> }
        operations[Opcode.HALT] = { _, _ -> registers.pc = Int.MAX_VALUE }
    }

    fun process(executionInfo: ExecutionInfo) {
        process(executionInfo.opcode, executionInfo.operand1, executionInfo.operand2)
    }

    fun process(opcode: Opcode, operand1: Int, operand2: Int) {
        operations[opcode]?.invoke(operand1, operand2)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed")
    }
}
