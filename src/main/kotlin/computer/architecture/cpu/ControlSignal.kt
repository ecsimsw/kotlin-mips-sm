package computer.architecture.cpu

data class ControlSignal(
    val opcode: Opcode = Opcode.SLL,
    val regDest: Boolean = opcode.type == Opcode.Type.R,
    val aluSrc: Boolean = (opcode.type != Opcode.Type.R)
            && (opcode != Opcode.BEQ)
            && (opcode != Opcode.BNE),
    val shift: Boolean = opcode == Opcode.SLL,
    val upperImm: Boolean = opcode == Opcode.LUI,
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
    val jal: Boolean = (opcode == Opcode.JAL),
    val aluOp: AluOp = opcode.operation,
    var isEnd: Boolean = false
) {
    companion object {
        val NONE = ControlSignal(regWrite = false, memWrite = false, isEnd = false)
    }
}
