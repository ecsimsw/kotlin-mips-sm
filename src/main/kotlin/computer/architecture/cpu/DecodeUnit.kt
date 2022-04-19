package computer.architecture.cpu

class DecodeUnit {
    fun decode(instruction: Int): InstructionDecodeResult {
        return InstructionDecodeResult(
            opcode = Opcode.of(instruction shr 26 and 0x3F, instruction and 0x3F),
            rs = instruction shr 21 and 0x1F,
            rt = instruction shr 16 and 0x1F,
            rd = instruction shr 11 and 0x1F,
            shiftAmt = instruction shr 5 and 0x1F,
            immediate = instruction and 0xFFFF,
            address = instruction and 0x3FFFFFF
        )
    }
}

data class InstructionDecodeResult(
    val opcode: Opcode,
    val rs: Int,
    val rt: Int,
    val rd: Int,
    val shiftAmt: Int,
    val immediate: Int,
    val address: Int,
)
