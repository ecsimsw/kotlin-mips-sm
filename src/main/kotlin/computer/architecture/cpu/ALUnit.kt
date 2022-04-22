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
        operations[Opcode.SW] = { op1, op2 -> (op1 + op2) }
        operations[Opcode.LW] = { op1, op2 -> (op1 + op2) }
        operations[Opcode.LUI] = { _, imm -> imm shl 16 }
        operations[Opcode.JR] = { _, _ -> 0 }
        operations[Opcode.J] = { _, _ -> 0 }
        operations[Opcode.JAL] = { _, _ -> 0 }
        operations[Opcode.BNE] = { op1, op2 -> if (op1 != op2) 1 else 0 }
        operations[Opcode.BEQ] = { op1, op2 -> if (op1 == op2) 1 else 0 }
    }

    fun operate(opcode: Opcode, shiftAmt: Int, src1: Int, src2: Int): ALUResult {
        val arguments = arguments(opcode, shiftAmt, src1, src2)
        val result = operations[opcode]?.invoke(arguments.first, arguments.second)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed : $opcode")
        return ALUResult(result)
    }

    private fun arguments(opcode: Opcode, shiftAmt: Int, src1: Int, src2: Int): Pair<Int, Int> {
        if (opcode == Opcode.SLL) {
            return Pair(src1, shiftAmt)
        }

        if (opcode == Opcode.ADDI
            || opcode == Opcode.ADDIU
            || opcode == Opcode.ANDI
            || opcode == Opcode.SLTI
            || opcode == Opcode.SLTIU
            || opcode == Opcode.SW
            || opcode == Opcode.LW
        ) return Pair(src1, signExtension32(src2))

        if (opcode == Opcode.ORI) {
            return Pair(src1, zeroExtension32(src2))
        }
        return Pair(src1, src2)
    }

    private fun signExtension32(num: Int): Int {
        return (num shl 16) shr 16
    }

    private fun zeroExtension32(num: Int): Int {
        return num shl 16 ushr 16
    }
}

data class ALUResult(
    val resultValue: Int,
    val isZero: Boolean = resultValue == 0
)
