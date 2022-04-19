package computer.architecture.cpu

class ALUnit(
    private val operations: MutableMap<Opcode, (Int, Int) -> Int> = mutableMapOf()
) {

    init {
        operations[Opcode.ADDIU] = { op1, op2 -> op1 + op2 }
        operations[Opcode.ADDU] = { op1, op2 -> op1 + op2 }
        operations[Opcode.ADDI] = { op1, op2 -> op1 + op2 }
        operations[Opcode.SLL] = { op1, shiftAmt -> op1 shl shiftAmt }
        operations[Opcode.SW] = { op1, op2 -> (op1 + op2)/4 }
        operations[Opcode.LW] = { op1, op2 -> (op1 + op2)/4 }
        operations[Opcode.JR] = { _, _ -> 0 }
    }

    fun operate(aluControl: ALUControl, src1: Int, src2: Int): ALUResult {
        val operation = operations[aluControl.opcode]
            ?: throw IllegalArgumentException("Opcodes that cannot be computed")

        Thread.sleep(1000)

        if(aluControl.opcode == Opcode.SLL) {
            return ALUResult(operation.invoke(src1, aluControl.shiftAmt))
        }
        return ALUResult(operation.invoke(src1, src2))
    }
}

data class ALUControl(
    val opcode: Opcode,
    val shiftAmt :Int
)

data class ALUResult(
    val resultValue: Int,
    val isZero: Boolean = resultValue == 0
)
