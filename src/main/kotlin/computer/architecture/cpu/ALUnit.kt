package computer.architecture.cpu

class ALUnit(
    private val operations: MutableMap<AluOp, (Int, Int) -> Int> = mutableMapOf()
) {

    init {
        operations[AluOp.ADDITION] = { src1, src2 -> src1 + src2 }
        operations[AluOp.SUBTRACTION] = { src1, src2 -> src1 - src2 }
        operations[AluOp.OR] = { src1, src2 -> src1 or src2 }
        operations[AluOp.SHIFT_LEFT] = { src1, src2 -> src1 shl src2 }
        operations[AluOp.SET_LESS_THAN] = { src1, src2 -> if (src1 < src2) 1 else 0 }
        operations[AluOp.NONE] = { _, _ -> 0 }
        operations[AluOp.EQUAL] = { src1, src2 -> if (src1 == src2) 1 else 0 }
        operations[AluOp.NOT_EQUAL] = { src1, src2 -> if (src1 != src2) 1 else 0 }
    }

    fun operate(aluOp: AluOp, src1: Int, src2: Int): AluResult {
        return AluResult(operations[aluOp]?.invoke(src1, src2)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed : $aluOp"))
    }
}

data class AluResult(
    val value: Int,
    val isTrue: Boolean = value == 1
)

enum class AluOp {
    ADDITION,
    SUBTRACTION,
    OR,
    SHIFT_LEFT,
    SET_LESS_THAN,
    NONE,
    EQUAL,
    NOT_EQUAL;
}
