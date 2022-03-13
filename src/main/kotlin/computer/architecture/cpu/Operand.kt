package computer.architecture.cpu

class Operand(
    operand: String
) {
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
