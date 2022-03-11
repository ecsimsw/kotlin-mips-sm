package computer.architecture.cpu

import java.lang.IllegalArgumentException

enum class Opcode(val code: String) {
    ADD("+"),
    MINUS("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    JUMP("J"),
    BRANCH("B"),
    CONDITION("C"),
    MOVE("M"),
    HALT("H");

    companion object {
        fun of(code:String) : Opcode {
            return values().find { it.code == code }
                ?: throw IllegalArgumentException("Non existence operation code : $code")
        }
    }
}
