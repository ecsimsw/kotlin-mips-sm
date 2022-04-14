package computer.architecture.single

import computer.architecture.utils.fillDigitsWith

class DecodeUnit {

    fun decode(instruction: Int): ExecutionInfo {
        val binaryInst = Integer.toBinaryString(instruction).fillDigitsWith(32, 0)
        return decode(binaryInst)
    }

    fun decode(binaryInst: String): ExecutionInfo {
        try {
            val opcode = binaryInst.substring(0, 6)
            val rs = binaryInst.substring(6, 11)
            val rt = binaryInst.substring(11, 16)
            val rd = binaryInst.substring(16, 21)
            val shamt = binaryInst.substring(21, 26)
            val funct = binaryInst.substring(26, 32)
            val imm = binaryInst.substring(16, 32)
            val addr = binaryInst.substring(6, 32)
            return ExecutionInfo(Opcode.of(opcode, funct), rs, rt, rd, shamt, funct, imm, addr)
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            throw IllegalArgumentException("Invalid instruction : $binaryInst")
        }
    }
}

data class ExecutionInfo(
    val opcode: Opcode,
    val rs: String,
    val rt: String,
    val rd: String,
    val shamt: String,
    val funct: String,
    val imm: String,
    val addr: String,
)
