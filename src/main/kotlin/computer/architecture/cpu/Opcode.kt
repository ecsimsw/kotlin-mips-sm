package computer.architecture.cpu

import computer.architecture.utils.toHexString

enum class Opcode(
    val code: Int,
    val type: Type,
    val operation: AluOp
) {
    ADDI(0x08, Type.I, AluOp.ADDITION),
    ADDIU(0x09, Type.I, AluOp.ADDITION),
    ADDU(0x21, Type.R, AluOp.ADDITION),
    BEQ(0x04, Type.I, AluOp.EQUAL),
    BNE(0x05, Type.I, AluOp.NOT_EQUAL),
    J(0x02, Type.J, AluOp.NONE),
    JAL(0x03, Type.J, AluOp.NONE),
    JR(0x08, Type.R, AluOp.NONE),
    LUI(0x0F, Type.I, AluOp.SHIFT_LEFT),
    LW(0x23, Type.I, AluOp.ADDITION),
    ORI(0x0D, Type.I, AluOp.OR),
    SLT(0x2A, Type.R, AluOp.SET_LESS_THAN),
    SLTI(0x0A, Type.I, AluOp.SET_LESS_THAN),
    SLL(0x00, Type.R, AluOp.SHIFT_LEFT),
    SW(0x2B, Type.I, AluOp.ADDITION),
    SUBU(0x23, Type.R, AluOp.SUBTRACTION);

    enum class Type { R, I, J }

    companion object {

        fun of(instruction: Int): Opcode {
            try {
                return of(instruction shr 26 and 0x3F, instruction and 0x3F)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid opcode!! : instruction : 0x${instruction.toHexString(8)}\n ${e.message}")
            }
        }

        private fun of(op: Int, func: Int): Opcode {
            return values().find {
                if (op == 0)
                    it.type == Type.R && it.code == func
                else
                    it.type != Type.R && it.code == op
            } ?: throw IllegalArgumentException(
                "opcode : ${Integer.toBinaryString(op)}, function : ${Integer.toBinaryString(func)}"
            )
        }
    }
}
