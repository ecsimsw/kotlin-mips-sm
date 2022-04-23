package computer.architecture.cpu

class DecodeUnit {

    fun decodeInstruction(instruction: Int): DecodedInstruction {
        return DecodedInstruction(instruction)
    }
}

data class DecodedInstruction(
    private val instruction: Int,
    val opcode: Opcode = Opcode.of(instruction shr 26 and 0x3F, instruction and 0x3F),
    val rs: Int = instruction shr 21 and 0x1F,
    val rt: Int = instruction shr 16 and 0x1F,
    val rd: Int = instruction shr 11 and 0x1F,
    val shiftAmt: Int = instruction shr 6 and 0x1F,
    val immediate: Int = instruction and 0xFFFF,
    val address: Int = instruction and 0x3FFFFFF
)

data class ControlSignal(
    val opcode: Opcode = Opcode.SLL,
    val regDest: Boolean = opcode.type == Opcode.Type.R,
    val aluSrc: Boolean = (opcode.type != Opcode.Type.R)
            && (opcode != Opcode.BEQ)
            && (opcode != Opcode.BNE),
    val shift: Boolean = opcode == Opcode.SLL,
    val memToReg: Boolean = opcode == Opcode.LW,
    val regWrite: Boolean = (opcode != Opcode.SW) &&
            (opcode != Opcode.BEQ) &&
            (opcode != Opcode.BNE) &&
            (opcode != Opcode.J) &&
            (opcode != Opcode.JR),
    val memRead: Boolean = opcode == Opcode.LW,
    val memWrite: Boolean = opcode == Opcode.SW,
    val jump: Boolean = (opcode == Opcode.J) || (opcode == Opcode.JAL),
    val branch: Boolean = (opcode == Opcode.BNE || opcode == Opcode.BEQ),
    val jr: Boolean = (opcode == Opcode.JR),
    val jal: Boolean = (opcode == Opcode.JAL)
)
