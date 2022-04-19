package computer.architecture.cpu

data class ControlSignal(
    val opcode: Opcode,
    val aluOp: Opcode = opcode,
    val regDest: Boolean = opcode.type == Opcode.Type.R,
    val aluSrc: Boolean = (opcode.type != Opcode.Type.R)
            && (opcode != Opcode.BEQ)
            && (opcode != Opcode.BNE),
    val memToReg: Boolean = opcode == Opcode.LW,
    val regWrite: Boolean = (opcode != Opcode.SW) &&
            (opcode != Opcode.BEQ) &&
            (opcode != Opcode.BNE) &&
            (opcode != Opcode.J) &&
            (opcode != Opcode.JR),
    val memRead: Boolean = opcode == Opcode.LW,
    val memWrite: Boolean = opcode == Opcode.SW,
    val pcSrc1: Boolean = (opcode == Opcode.J) || (opcode == Opcode.JAL),
    val pcSrc2: Boolean = (opcode == Opcode.BNE),
    val jumpRegister: Boolean = (opcode == Opcode.JR)
)
