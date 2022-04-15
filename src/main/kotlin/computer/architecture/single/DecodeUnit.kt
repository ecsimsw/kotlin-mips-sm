package computer.architecture.single

import computer.architecture.single.Mux.Companion.mux
import computer.architecture.single.log.Logger

class DecodeUnit(
    private val controlSignal: ControlSignal,
    private val registers: Registers
) {
    fun decode(instruction: Int): DecodeResult {
        val op = instruction shr 26 and 0x3F
        val rs = instruction shr 21 and 0x1F
        val rt = instruction shr 16 and 0x1F
        val rd = instruction shr 11 and 0x1F
        val shiftAmt = instruction shr 5 and 0x1F
        val function = instruction and 0x3F
        val immediate = instruction and 0xFFFF
        val address = instruction and 0x3FFFFFF
        val opcode = Opcode.of(op, function)
        val writeRegister = mux(controlSignal.regWrite, rt, rd)

        Logger.decodeLog(op, function, rs, rt, rd, shiftAmt, immediate, address)

        controlSignal.setSignals(opcode)

        return DecodeResult(
            opcode,
            writeRegister,
            shiftAmt,
            immediate,
            address,
            registers[rs],
            registers[rt]
        )
    }
}

data class DecodeResult(
    val opcode: Opcode,
    val writeRegister: Int,
    val shiftAmt: Int,
    val immediate: Int,
    val address: Int,
    val readData1: Int,
    val readData2: Int
)
