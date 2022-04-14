package computer.architecture.single

import computer.architecture.cpu.Registers

class DecodeUnit{
    var opcode = ""
    var rs = ""
    var rt = ""
    var rd = ""
    var shamt = ""
    var funct = ""
    var imm = ""
    var addr = ""

    fun decode(instruction: String): Unit {
        opcode = instruction.substring(0, 6)
        rs = instruction.substring(6, 11)
        rt = instruction.substring(11, 16)
        rd = instruction.substring(16, 21)
        shamt = instruction.substring(21, 26)
        funct = instruction.substring(26, 32)
        imm = instruction.substring(16, 32)
        addr = instruction.substring(6, 32)
    }
}
