package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers

class DecodeUnit(
    private val registers: Registers
) {
    fun decode(instruction: Int): DecodeResult {
        val opcode = Opcode.of(instruction shr 26 and 0x3F, instruction and 0x3F)
        val rs = instruction shr 21 and 0x1F
        val rt = instruction shr 16 and 0x1F
        val rd = instruction shr 11 and 0x1F

        val controlSignal = ControlSignal(opcode)
        val writeRegister = mux(controlSignal.regWrite, rt, rd)

        return DecodeResult(
            opcode = opcode,
            shiftAmt = instruction shr 5 and 0x1F,
            immediate = instruction and 0xFFFF,
            address = instruction and 0x3FFFFFF,
            readData1 = registers[rs],
            readData2 = registers[rt],
            writeRegister = writeRegister,
            controlSignal = controlSignal
        )
    }
}
