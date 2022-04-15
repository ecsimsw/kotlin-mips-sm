package computer.architecture.single

class DecodeUnit {

    fun decode(intInst: Int): ExecutionInfo {
        val opcode = intInst shr 26 and 0x3F
        val rs = intInst shr 21 and 0x1F
        val rt = intInst shr 16 and 0x1F
        val rd = intInst shr 11 and 0x1F
        val shiftAmt = intInst shr 5 and 0x1F
        val function = intInst and 0x3F
        val immediate = intInst and 0xFFFF
        val address = intInst and 0x3FFFFFF
        return ExecutionInfo(Opcode.of(opcode, function), rs, rt, rd, shiftAmt, function, immediate, address)
    }
}

data class ExecutionInfo(
    val opcode: Opcode,
    val rs: Int,
    val rt: Int,
    val rd: Int,
    val shiftAmt: Int,
    val function: Int,
    val immediate: Int,
    val address: Int,
)
