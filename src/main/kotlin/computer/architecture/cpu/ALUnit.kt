package computer.architecture.cpu

class ALUnit(
    private val operations: MutableMap<Opcode, (Int, Int) -> Int> = mutableMapOf()
) {

    init {
        operations[Opcode.ADDIU] = { op1, op2 -> op1 + op2 }
        operations[Opcode.SLTU] = { op1, op2 -> op1 + op2 }
    }

    fun operate(aluOp: Opcode, src1: Int, src2: Int): ALUResult {
        val operation = operations[aluOp]
            ?: throw IllegalArgumentException("Opcodes that cannot be computed")
        return ALUResult(operation.invoke(src1, src2))
    }
}

data class ALUResult(
    val resultValue: Int,
    val isZero: Boolean = resultValue == 0
)
