package computer.architecture.cpu

class Operand(
    private val operand: String
) {
    companion object {
        private const val PREFIX_REGISTER_NUMBER = "R"
        private const val PREFIX_HEXADECIMAL_NUMBER = "0x"

        fun of(operand: String) = Operand(operand)
    }

    fun isRegisterNumber() = operand.startsWith(PREFIX_REGISTER_NUMBER)

    fun isHexadecimalNumber() = operand.startsWith(PREFIX_HEXADECIMAL_NUMBER)

    fun registerNumber(): Int {
        if (isRegisterNumber()) {
            return operand.substring(PREFIX_REGISTER_NUMBER.length).toInt()
        }
        throw IllegalArgumentException("This is not the register number operand")
    }

    fun getIntValue(): Int {
        if (isHexadecimalNumber()) {
            return Integer.decode(operand.substring(PREFIX_HEXADECIMAL_NUMBER.length))
        }
        throw IllegalArgumentException("This is not the number value operand")
    }
}
