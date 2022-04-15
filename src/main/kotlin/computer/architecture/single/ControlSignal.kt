package computer.architecture.single

class ControlSignal {
    var aluOp = Opcode.SRL
    var regDest = false
    var aluSrc = false
    var memToReg = false
    var regWrite = false
    var memRead = false
    var memWrite = false
    var pcSrc1 = false
    var pcSrc2 = false

    fun setSignals(opcode: Opcode) {
        aluOp = opcode
        regDest = opcode.type == Opcode.Type.R
        aluSrc = (opcode.type != Opcode.Type.R)
                && (opcode != Opcode.BEQ)
                && (opcode != Opcode.BNE)
        memToReg = opcode == Opcode.LW
        regWrite = (opcode != Opcode.SW) &&
                (opcode != Opcode.BEQ) &&
                (opcode != Opcode.BNE) &&
                (opcode != Opcode.J) &&
                (opcode != Opcode.JR)
        memRead = opcode == Opcode.LW
        memWrite = opcode == Opcode.SW
        pcSrc1 = (opcode == Opcode.J) || (opcode == Opcode.JAL)
//        pcSrc2 = (opcode==Opcode.BNE) &&
    }
}
