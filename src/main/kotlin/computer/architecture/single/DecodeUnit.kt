package computer.architecture.single

import computer.architecture.single.log.Logger

class DecodeUnit {

    fun decode(instruction: Int): ExecutionInfo {
        val opcode = instruction shr 26 and 0x3F
        val rs = instruction shr 21 and 0x1F
        val rt = instruction shr 16 and 0x1F
        val rd = instruction shr 11 and 0x1F
        val shiftAmt = instruction shr 5 and 0x1F
        val function = instruction and 0x3F
        val immediate = instruction and 0xFFFF
        val address = instruction and 0x3FFFFFF

        Logger.decodeLog(opcode, function, rs, rt, rd, shiftAmt, immediate, address)

        return ExecutionInfo(
            Opcode.of(opcode, function), rs, rt, rd, shiftAmt, function, immediate, address
        )
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
