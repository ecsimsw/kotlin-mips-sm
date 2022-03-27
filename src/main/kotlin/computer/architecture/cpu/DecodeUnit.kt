package computer.architecture.cpu

class DecodeUnit(
    val registers: Registers
) {
    fun decode(instruction: String): ExecutionInfo {
        if (instruction == "" || instruction.startsWith("//"))
            return ExecutionInfo(Opcode.ANNOTATION, Operand.NONE, Operand.NONE)
        return ExecutionInfo.of(instruction)
    }
}

data class ExecutionInfo(
    val opcode: Opcode,
    val operand1: Operand,
    val operand2: Operand
) {
    companion object {
        fun of(instruction: String): ExecutionInfo {
            try {
                val split = instruction.split(" ")
                return of(split[0], split[1], split[2])
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid instruction format : $instruction \n" + "${e.message}")
            }
        }

        private fun of(opcode: String, operand1: String, operand2: String): ExecutionInfo {
            return ExecutionInfo(Opcode.of(opcode), Operand(operand1), Operand(operand2))
        }
    }
}

enum class Opcode(val code: String) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MOD("%"),
    SLL("<<"),
    SRL(">>"),
    AND("&&"),
    OR("||"),
    JUMP("J"),
    JUMP_AND_LINK("JAL"),
    BRANCH("B"),
    BRANCH_ON_EQUAL("BEQ"),
    BRANCH_ON_NOT_EQUAL("BNE"),
    CONDITION("C"),
    MOVE("M"),
    LOAD_WORD("LW"),
    STORE_WORD("SW"),
    ANNOTATION("//"),
    HALT("H");

    companion object {
        fun of(code: String) = values().find { it.code == code }
            ?: throw IllegalArgumentException("Non existence operation code : $code")
    }
}

class Operand(
    operand: String
) {
    companion object {
        val NONE = Operand("0x00")
    }

    val type: OperandType = OperandType.of(operand)
    val number: Int = OperandType.number(operand)

    fun registerNumber(): Int {
        if (type == OperandType.REGISTER_NUMBER) {
            return number
        }
        throw IllegalArgumentException("Not register number operand")
    }
}

enum class OperandType(
    private val prefix: String
) {
    REGISTER_NUMBER("R"), HEXADECIMAL_NUMBER("0x");

    companion object {
        fun of(operand: String) = values().find { operand.startsWith(it.prefix) }
            ?: throw IllegalArgumentException("Invalid format of operand")

        fun number(operand: String): Int {
            val type = of(operand)
            if (type == HEXADECIMAL_NUMBER) {
                return Integer.decode(operand)
            }
            return operand.substring(type.prefix.length).toInt()
        }
    }
}
