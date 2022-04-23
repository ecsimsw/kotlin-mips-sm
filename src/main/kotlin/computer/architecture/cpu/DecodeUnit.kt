package computer.architecture.cpu

class DecodeUnit {

    fun parse(pc: Int, instruction: Int) = ParsedInstruction(pc, instruction)

    fun controlSignal(opcode: Opcode) = ControlSignal(opcode)
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

        fun immediate(pc: Int, instruction: Int): Int {
            val originImm = instruction and 0xFFFF
            return when (Opcode.of(instruction)) {
                Opcode.ADDIU,
                Opcode.ADDI,
                Opcode.SLTI,
                Opcode.SW,
                Opcode.LW -> signExtension32(originImm)
                Opcode.ORI -> zeroExtension32(originImm)
                Opcode.BNE,
                Opcode.BEQ -> pc + branchAddress(originImm)
                else -> originImm
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

data class ControlSignal(
    val opcode: Opcode = Opcode.SLL,
    val regDest: Boolean = opcode.type == Opcode.Type.R,
    val aluSrc: Boolean = (opcode.type != Opcode.Type.R)
            && (opcode != Opcode.BEQ)
            && (opcode != Opcode.BNE),
    val shift: Boolean = opcode == Opcode.SLL,
    val upperImm: Boolean = opcode == Opcode.LUI,
    val memToReg: Boolean = opcode == Opcode.LW,
    val regWrite: Boolean = (opcode != Opcode.SW) &&
            (opcode != Opcode.BEQ) &&
            (opcode != Opcode.BNE) &&
            (opcode != Opcode.J) &&
            (opcode != Opcode.JR),
    val memRead: Boolean = opcode == Opcode.LW,
    val memWrite: Boolean = opcode == Opcode.SW,
    val jump: Boolean = (opcode == Opcode.J) || (opcode == Opcode.JAL),
    val branch: Boolean = (opcode == Opcode.BNE || opcode == Opcode.BEQ),
    val jr: Boolean = (opcode == Opcode.JR),
    val jal: Boolean = (opcode == Opcode.JAL),
    val aluOp: AluOp = opcode.operation
)
