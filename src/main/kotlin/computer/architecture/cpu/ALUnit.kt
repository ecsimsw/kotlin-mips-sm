package computer.architecture.cpu

class ALUnit(
    private val controlSignal: ControlSignal,
    private val operations: MutableMap<Opcode, (Int, Int) -> Int> = mutableMapOf()
) {

    init {

    }

    fun operate(src1: Int, src2: Int): ALUResult {
        val operation = operations[controlSignal.aluOp]
            ?: throw IllegalArgumentException("Opcodes that cannot be computed")
        return ALUResult(operation.invoke(src1, src2))
    }
}

data class ALUResult(
    val resultValue: Int,
    val isZero: Boolean = resultValue == 0
)
