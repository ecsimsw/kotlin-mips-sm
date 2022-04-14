package computer.architecture.single

class DecodeUnit {

    fun decode(instruction: String): ExecutionInfo {
        try {
            val opcode = instruction.substring(0, 6)
            val rs = instruction.substring(6, 11)
            val rt = instruction.substring(11, 16)
            val rd = instruction.substring(16, 21)
            val shamt = instruction.substring(21, 26)
            val funct = instruction.substring(26, 32)
            val imm = instruction.substring(16, 32)
            val addr = instruction.substring(6, 32)
            return ExecutionInfo(Opcode.of(opcode, funct), rs, rt, rd, shamt, funct, imm, addr)
        } catch (e: StringIndexOutOfBoundsException) {
            e.printStackTrace()
            throw IllegalArgumentException("Invalid instruction : $instruction")
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
