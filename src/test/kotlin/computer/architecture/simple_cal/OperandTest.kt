package computer.architecture.simple_cal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class OperandTest {

    @DisplayName("16진수의 피연산자를 10진수 정수를 받는다")
    @Test
    fun parseHexNumber() {
        val operand = Operand("0x15")
        assertThat(operand.number).isEqualTo(21)
    }

    @DisplayName("피연산자가 레지스터일 경우 레지스터 번호를 반환한다.")
    @Test
    fun parseRegisterNumber() {
        val operand = Operand("R9")
        assertThat(operand.number).isEqualTo(9)
    }

    @DisplayName("Operand가 유효한 prefix를 갖고 있는지 확인한다")
    @ParameterizedTest
    @ValueSource(strings = ["R1", "0x1", "0xA"])
    fun validInputFormat(value: String) {
        assertDoesNotThrow { Operand(value) }
    }

    @DisplayName("유효하지 않은 형태의 operand에 예외를 발생한다")
    @ParameterizedTest
    @ValueSource(strings = ["", "a", "R0x1", "0xx1", "0xz"])
    fun invalidInputFormat(value: String) {
        assertThrows<IllegalArgumentException> { Operand(value) }
    }
}
