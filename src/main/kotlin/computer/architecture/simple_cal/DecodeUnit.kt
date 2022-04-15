package computer.architecture.simple_cal

class DecodeUnit(
    private val registers: Registers
) {
    fun decode(instruction: String): ExecutionInfo {
        if (instruction.trim() == "" || instruction.startsWith("//")) {
            return ExecutionInfo.ANNOTATION
        }
        try {
            val split = instruction.split(" ")
            val opcode = Opcode.of(split[0])
            val operand1 = Operand(split[1])
            val operand2 = Operand(split[2])

            if (opcode == Opcode.JUMP_AND_LINK || opcode == Opcode.MOVE) {
                return ExecutionInfo(opcode, operand1.registerNumber(), value(operand2))
            }
            return ExecutionInfo(opcode, value(operand1), value(operand2))
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid instruction format : $instruction \n" + "${e.message}")
        }
    }

    private fun value(operand: Operand): Int {
        if (operand.type == OperandType.REGISTER_NUMBER) {
            return registers.r[operand.number]
        }
        if (operand.type == OperandType.HEXADECIMAL_NUMBER) {
            return operand.number
        }
        throw IllegalArgumentException("This is not the format of operand")
    }
}

data class ExecutionInfo(
    val opcode: Opcode,
    val operand1: Int,
    val operand2: Int
) {
    companion object {
        val ANNOTATION = ExecutionInfo(Opcode.ANNOTATION, 0, 0)
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
        fun of(code: String): Opcode {
            return values().find { it.code == code }
                ?: throw IllegalArgumentException("Non existence operation code : $code")
        }
    }
}

class Operand(
    operand: String
) {
    val type: OperandType = OperandType.of(operand)
    val number: Int = number(operand)

    fun registerNumber(): Int {
        if (type == OperandType.REGISTER_NUMBER) {
            return number
        }
        throw IllegalArgumentException("Not register number operand")
    }

    private fun number(operand: String): Int {
        val type = OperandType.of(operand)
        if (type == OperandType.HEXADECIMAL_NUMBER) {
            return Integer.decode(operand)
        }
        return operand.substring(type.prefix.length).toInt()
    }
}

enum class OperandType(
    val prefix: String
) {
    REGISTER_NUMBER("R"),
    HEXADECIMAL_NUMBER("0x");

    companion object {
        fun of(operand: String) = values().find { operand.startsWith(it.prefix) }
            ?: throw IllegalArgumentException("Invalid format of operand")
    }
}
