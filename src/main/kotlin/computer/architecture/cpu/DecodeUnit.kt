package computer.architecture.cpu

class DecodeUnit {

    fun parse(pc: Int, instruction: Int): ParsedInstruction {
        if(instruction == 0) {
            return ParsedInstruction.NOP
        }
        return ParsedInstruction(pc, instruction)
    }

    fun controlSignal(opcode: Opcode) = controlSignal(true, opcode)

    fun controlSignal(valid: Boolean, opcode: Opcode): ControlSignal {
        if(valid) {
            return ControlSignal(opcode)
        }
        return ControlSignal.NONE
    }
}

data class ParsedInstruction(
    private val pc: Int,
    private val instruction: Int,
    val opcode: Opcode = Opcode.of(instruction),
    val rs: Int = instruction shr 21 and 0x1F,
    val rt: Int = instruction shr 16 and 0x1F,
    val rd: Int = instruction shr 11 and 0x1F,
    val shiftAmt: Int = instruction shr 6 and 0x1F,
    val immediate: Int = immediate(pc, instruction),
    val address: Int = address(pc, instruction)
) {
    companion object {

        val NOP = ParsedInstruction(0, 0, )

        fun immediate(pc: Int, instruction: Int): Int {
            val imm = instruction and 0xFFFF
            return when (Opcode.of(instruction)) {
                Opcode.ADDIU,
                Opcode.ADDI,
                Opcode.SLTI,
                Opcode.SW,
                Opcode.LW -> signExtension32(imm)
                Opcode.ORI -> zeroExtension32(imm)
                Opcode.BNE,
                Opcode.BEQ -> pc + branchAddress(imm)
                else -> imm
            }
        }

        fun address(pc: Int, instruction: Int): Int {
            val originAddress = instruction and 0x3FFFFFF
            return when (Opcode.of(instruction)) {
                Opcode.J,
                Opcode.JAL -> jumpAddress(pc, originAddress)
                else -> originAddress
            }
        }

        private fun signExtension32(num: Int) = num shl 16 shr 16

        private fun zeroExtension32(num: Int) = num shl 16 ushr 16

        private fun jumpAddress(pc: Int, address: Int): Int {
            val first4bit = (pc shr 28 and 0xF).toBinaryString(4)
            val last28bit = address.toBinaryString(26) + "00"
            return (first4bit + last28bit).toLong(2).toInt()
        }

        private fun branchAddress(immediate: Int): Int {
            val binaryImmediate = immediate.toBinaryString(16)
            return if (binaryImmediate.first() == '1') {
                ("11111111111111" + binaryImmediate + "00").toLong(2).toInt()
            } else {
                ("00000000000000" + binaryImmediate + "00").toLong(2).toInt()
            }
        }

        private fun Int.toBinaryString(digits: Int): String {
            val originNumber = Integer.toBinaryString(this)
            var newBinary = originNumber
            if (newBinary.length < digits) {
                for (i in 0 until digits - originNumber.length) {
                    newBinary = "0$newBinary"
                }
            }
            return newBinary
        }
    }
}
