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
            signExtImm = signExtension32(instruction and 0xFFFF, 16),
            address = instruction and 0x3FFFFFF,
        )
    }

    private fun signExtension32(num : Int, nowBit : Int): Int {
        return (num shl (32-nowBit)) shr (32 - nowBit)
    }
}

data class InstructionDecodeResult(
    val opcode: Opcode,
    val rs: Int,
    val rt: Int,
    val rd: Int,
    val shiftAmt: Int,
    val immediate: Int,
    val signExtImm: Int,
    val address: Int
)
