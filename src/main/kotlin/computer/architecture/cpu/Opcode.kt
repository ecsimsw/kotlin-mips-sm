package computer.architecture.cpu

enum class Opcode(val code: String) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MOD("%"),
    POWER("^"),
    SLL("<<"),
    SRL(">>"),
    AND("&&"),
    OR("||"),
    JUMP("J"),
    BRANCH("B"),
    BRANCH_ON_EQUAL("BEQ"),
    BRANCH_ON_NOT_EQUAL("BNE"),
    CONDITION("<"),
    MOVE("M"),
    HALT("H");

    companion object {
        fun of(code: String) = values().find { it.code == code }
            ?: throw IllegalArgumentException("Non existence operation code : $code")
    }
}
