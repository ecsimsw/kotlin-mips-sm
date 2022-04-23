package computer.architecture.cpu

enum class Opcode(
    val type: Type,
    val code: Int
) {
    ADDI(Type.I, 0x08),
    ADDIU(Type.I, 0x09),
    ADDU(Type.R, 0x21),
    BEQ(Type.I, 0x04),
    BNE(Type.I, 0x05),
    J(Type.J, 0x02),
    JAL(Type.J, 0x03),
    JR(Type.R, 0x08),
    LUI(Type.I, 0x0F),
    LW(Type.I, 0x23),
    ORI(Type.I, 0x0D),
    SLT(Type.R, 0x2A),
    SLTI(Type.I, 0x0A),
    SLL(Type.R, 0x00),
    SW(Type.I, 0x2B),
    SUBU(Type.R, 0x23);

    enum class Type { R, I, J }

    companion object {

        fun of(instruction: Int): Opcode {
            return of(instruction shr 26 and 0x3F, instruction and 0x3F)
        }

        private fun of(op: Int, func: Int): Opcode {
            return values().find {
                if (op == 0)
                    it.type == Type.R && it.code == func
                else
                    it.type != Type.R && it.code == op
            } ?: throw IllegalArgumentException(
                "Invalid opcode!! opcode : ${Integer.toBinaryString(op)}, function : ${Integer.toBinaryString(func)}"
            )
        }
    }
}
