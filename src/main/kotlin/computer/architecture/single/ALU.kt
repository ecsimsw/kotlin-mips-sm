package computer.architecture.single

class ALU {
    private val operations: MutableMap<Opcode, (Int, Int) -> Unit> = mutableMapOf()

//    init {
//        operations[Opcode.ADD] = { op1, op2 -> R[0] = op1 + op2 }
//        operations[Opcode.ADDI] = { op1, op2 -> R[0] = op1 - op2 }
//        operations[Opcode.ADDIU] = { op1, op2 -> R[0] = op1 * op2 }
//        operations[Opcode.ADDU] = { op1, op2 -> R[0] = op1 / op2 }
//        operations[Opcode.AND] = { op1, op2 -> R[0] = op1 % op2 }
//        operations[Opcode.SLL] = { op1, op2 -> R[0] = op1 shl op2 }
//        operations[Opcode.SRL] = { op1, op2 -> R[0] = op1 shr op2 }
//        operations[Opcode.AND] = { op1, op2 -> R[0] = op1 and op2 }
//        operations[Opcode.OR] = { op1, op2 -> R[0] = op1 or op2 }
//        operations[Opcode.ORI] = { op1, op2 -> R[0] = op1 or op2 }
//        operations[Opcode.SLT] = { op1, _ -> registers.pc = op1 }
//        operations[Opcode.SLTI] = { r1, address -> R[r1] = registers.pc; registers.pc = address }
//        operations[Opcode.SLTIU] = { op1, _ -> if (R[0] == 1) registers.pc = op1 }
//        operations[Opcode.SLTU] = { op1, op2 -> if (R[0] == op1) registers.pc = op2 }
//        operations[Opcode.SLL] = { op1, op2 -> if (R[0] != op1) registers.pc = op2 }
//        operations[Opcode.SRL] = { op1, op2 -> R[0] = op1 < op2 }
//        operations[Opcode.SB] = { r1, value -> R[r1] = value }
//        operations[Opcode.SC] = { op1, _ -> R[0] = memory[op1] }
//        operations[Opcode.SH] = { op1, op2 -> memory[op2] = op1 }
//        operations[Opcode.SW] = { _, _ -> }
//        operations[Opcode.SUB] = { _, _ -> registers.pc = Int.MAX_VALUE }
//        operations[Opcode.SUBU] = { _, _ -> registers.pc = Int.MAX_VALUE }
//    }

    fun process(opcode: Opcode, operand1: Int, operand2: Int) {
        operations[opcode]?.invoke(operand1, operand2)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed")
    }
}
