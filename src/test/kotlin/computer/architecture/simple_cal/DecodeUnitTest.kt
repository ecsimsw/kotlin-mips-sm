package computer.architecture.simple_cal

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class DecodeUnitTest {

    lateinit var registers: Registers
    lateinit var decodeUnit: DecodeUnit

    @BeforeEach
    private fun setUp() {
        registers = Registers(10)
        decodeUnit = DecodeUnit(registers)
    }

    @DisplayName("빈 문자열 또는 주석 처리된 명령어는 주석으로 해석한다")
    @ParameterizedTest
    @ValueSource(strings = ["", " ", "// GCD ::", "// JUMP to GCD"])
    fun decodeAnnotation(instruction: String) {
        val result = decodeUnit.decode(instruction)
        assertThat(result.opcode).isEqualTo(Opcode.ANNOTATION)
    }

    @DisplayName("명령어의 연산 코드를 해석한다")
    @Test
    fun decodeOpcode() {
        val result = decodeUnit.decode("+ 0x02 0x01")
        assertThat(result.opcode).isEqualTo(Opcode.ADD)
    }

    @DisplayName("명령어의 정수형 피연사자 값을 해석한다")
    @Test
    fun decodeOperandValue() {
        val result = decodeUnit.decode("+ 0x2A 0x01")
        assertThat(result.operand1).isEqualTo(42)
        assertThat(result.operand2).isEqualTo(1)
    }

    @DisplayName("명령어의 레지스터형 피연산자 값을 해석한다")
    @Test
    fun decodeRegisterOperand() {
        registers.r[2] = 10
        registers.r[9] = 1

        val result = decodeUnit.decode("+ R2 R9")
        assertThat(result.operand1).isEqualTo(10)
        assertThat(result.operand2).isEqualTo(1)
    }

    @DisplayName("레지스터형 피연산자 값을 요구하는 특수한 연산")
    @Nested
    inner class RegisterNeededOperation {

        @DisplayName("MOV의 첫번째 연산자는 레지스터형이어야 한다")
        @Test
        fun movRegisterOperand() {
            assertDoesNotThrow { decodeUnit.decode("M R2 R9") }
            assertThrows<IllegalArgumentException> {
                decodeUnit.decode("M 0x00 R9")
            }
        }

        @DisplayName("JAL의 첫번째 연산자는 레지스터형이어야 한다")
        @Test
        fun jalRegisterOperand() {
            assertDoesNotThrow { decodeUnit.decode("JAL R2 R9") }
            assertThrows<IllegalArgumentException> {
                decodeUnit.decode("JAL 0x00 R9")
            }
        }
    }
}