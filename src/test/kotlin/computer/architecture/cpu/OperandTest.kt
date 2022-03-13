package computer.architecture.cpu

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class OperandTest {

    @DisplayName("Operand가 유효한 prefix를 갖고 있는지 확인한다")
    @ParameterizedTest
    @ValueSource(strings = ["R1", "0x1", "0xA"])
    fun validInputFormat(value:String) {
        assertDoesNotThrow { Operand(value) }
    }

    @DisplayName("유효하지 않은 형태의 operand에 예외를 발생한다")
    @ParameterizedTest
    @ValueSource(strings = ["", "a", "R0x1", "0xx1", "0xz"])
    fun invalidInputFormat(value:String) {
        assertThrows<IllegalArgumentException> { Operand(value) }
    }
}
