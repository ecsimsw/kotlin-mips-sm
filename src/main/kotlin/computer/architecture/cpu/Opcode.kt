package computer.architecture.cpu

enum class Opcode(val code: String) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    POWER("^"),
    SLL("<<"),
    SRL(">>"),
    AND("&&"),
    OR("||"),
    JUMP("J"),
    BRANCH("B"),
    CONDITION("C"),
    MOVE("M"),
    HALT("H");

    companion object {
        fun of(code: String) = values().find { it.code == code }
            ?: throw IllegalArgumentException("Non existence operation code : $code")
    }
}
