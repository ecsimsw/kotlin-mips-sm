package computer.architecture.cpu

class ALUnit(
    private val operations: MutableMap<Opcode, (Int, Int) -> Int> = mutableMapOf()
) {

    init {
        operations[Opcode.ADDIU] = { op1, imm -> op1 + imm }
        operations[Opcode.ADDU] = { op1, op2 -> op1 + op2 }
        operations[Opcode.ADDI] = { op1, imm -> op1 + imm }
        operations[Opcode.SUBU] = { op1, op2 -> op1 - op2 }
        operations[Opcode.ORI] = { op1, imm -> op1 or imm }
        operations[Opcode.SLL] = { op1, shiftAmt -> op1 shl shiftAmt }
        operations[Opcode.SLT] = { op1, op2 -> if (op1 < op2) 1 else 0 }
        operations[Opcode.SLTI] = { op1, imm -> if (op1 < imm) 1 else 0 }
        operations[Opcode.SW] = { op1, imm -> op1 + imm }
        operations[Opcode.LW] = { op1, imm -> op1 + imm }
        operations[Opcode.LUI] = { _, imm -> imm shl 16 }
        operations[Opcode.JR] = { _, _ -> 0 }
        operations[Opcode.J] = { _, _ -> 0 }
        operations[Opcode.JAL] = { _, _ -> 0 }
        operations[Opcode.BNE] = { op1, op2 -> if (op1 != op2) 1 else 0 }
        operations[Opcode.BEQ] = { op1, op2 -> if (op1 == op2) 1 else 0 }
    }

    fun operate(opcode: Opcode, src1: Int, src2: Int): ALUResult {
        val result = operations[opcode]?.invoke(src1, src2)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed : $opcode")
        return ALUResult(result, (opcode == Opcode.BNE || opcode == Opcode.BEQ) && result == 1)
    }
}

data class ALUResult(
    val resultValue: Int = 0,
    val branchCondition: Boolean = false
)
