package computer.architecture.single

enum class Opcode(
    val type: Type,
    val code: String,
    val codeAsDec: Int = Integer.decode(code)
) {
    ADD(Type.R, "0x20"),
    ADDI(Type.I, "0x08"),
    ADDIU(Type.I, "0x09"),
    ADDU(Type.R, "0x21"),
    AND(Type.R, "0x24"),
    ANDI(Type.I, "0x0C"),
    BEQ(Type.I, "0x04"),
    BNE(Type.I, "0x05"),
    J(Type.J, "0x02"),
    JAL(Type.J, "0x03"),
    JR(Type.R, "0x08"),
    LBU(Type.I, "0x24"),
    LHU(Type.I, "0x25"),
    LL(Type.I, "0x30"),
    LUI(Type.I, "0x0F"),
    LW(Type.I, "0x23"),
    NOR(Type.R, "0x27"),
    OR(Type.R, "0x25"),
    ORI(Type.I, "0x0D"),
    SLT(Type.R, "0x2A"),
    SLTI(Type.I, "0x0A"),
    SLTIU(Type.I, "0x0B"),
    SLTU(Type.R, "0x2B"),
    SLL(Type.R, "0x00"),
    SRL(Type.R, "0x02"),
    SB(Type.I, "0x28"),
    SC(Type.I, "0x38"),
    SH(Type.I, "0x29"),
    SW(Type.I, "0x2B"),
    SUB(Type.R, "0x22"),
    SUBU(Type.R, "0x23");

    enum class Type { R, I, J }

    companion object {
        fun of(binaryOpcode: String, binaryFunct: String): Opcode {
            return values().find { isOpcode(it, binaryOpcode, binaryFunct) }
                ?: throw IllegalArgumentException("Invalid opcode!! opcode : $binaryOpcode funct : $binaryFunct")
        }

        private fun isOpcode(opcode: Opcode, binaryOpcode: String, binaryFunct: String): Boolean {
            if (binaryOpcode == "000000") {
                return opcode.codeAsDec == Integer.parseInt(binaryFunct, 2)
            }
            return opcode.codeAsDec == Integer.parseInt(binaryOpcode, 2)
        }
    }
}
