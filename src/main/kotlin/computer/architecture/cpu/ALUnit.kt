package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux

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

    fun execute(idResult: DecodeResult): Int {
        val operationResult = operate(idResult)
        return mux(idResult.controlSignal.jal, idResult.pc + 8, operationResult)
    }

    private fun operate(idResult: DecodeResult): Int {
        val signal = idResult.controlSignal
        var src1 = mux(signal.shift, idResult.readData2, idResult.readData1)
        src1 = mux(signal.upperImm, idResult.immediate, src1)

        var src2 = mux(signal.aluSrc, idResult.immediate, idResult.readData2)
        src2 = mux(signal.shift, idResult.shiftAmt, src2)
        src2 = mux(signal.upperImm, 16, src2)

        return operate(signal.aluOp, src1, src2)
    }

    fun operate(aluOp: AluOp, src1: Int, src2: Int): Int {
        return operations[aluOp]?.invoke(src1, src2)
            ?: throw IllegalArgumentException("Opcodes that cannot be computed : $aluOp")
    }
}

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
