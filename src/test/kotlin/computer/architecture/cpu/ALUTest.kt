package computer.architecture.cpu

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

internal class ALUTest {

    private lateinit var registers: Registers
    private lateinit var alu: ALU

    @BeforeEach
    fun setUp() {
        registers = Registers()
        alu = ALU(registers)
    }

    private fun process(opcode: Opcode, operand1: String, operand2: String) {
        alu.process(opcode, Operand(operand1), Operand(operand2))
    }

    @Nested
    inner class SystemOperation {

        @Test
        fun testHalt() {
            process(Opcode.HALT, "0x02", "0x02")
            assertThat(registers.pc).isEqualTo(Int.MAX_VALUE)
        }
    }

    @Nested
    inner class ArithmeticOperation {

        @Test
        fun testArithmeticOperation() {
            process(Opcode.ADD, "0x01", "0x02")
            assertThat(registers.r[0]).isEqualTo(3)

            process(Opcode.MINUS, "0x02", "0x01")
            assertThat(registers.r[0]).isEqualTo(1)

            process(Opcode.MULTIPLY, "0x01", "0x02")
            assertThat(registers.r[0]).isEqualTo(2)

            process(Opcode.DIVIDE, "0x02", "0x01")
            assertThat(registers.r[0]).isEqualTo(2)

            process(Opcode.MOD, "0x04", "0x03")
            assertThat(registers.r[0]).isEqualTo(1)

            process(Opcode.POWER, "0x0A", "0x02")
            assertThat(registers.r[0]).isEqualTo(100)
        }

        @Test
        fun testBitwiseOperation() {
            process(Opcode.AND, "0x0C", "0x19")
            assertThat(registers.r[0]).isEqualTo(8)

            process(Opcode.OR, "0x0C", "0x19")
            assertThat(registers.r[0]).isEqualTo(29)

            process(Opcode.SLL, "0xD4", "0x01")
            assertThat(registers.r[0]).isEqualTo(424)

            process(Opcode.SRL, "0xD4", "0x01")
            assertThat(registers.r[0]).isEqualTo(106)
        }

        @Test
        fun operateWithRegister() {
            registers.r[1] = 1
            registers.r[2] = 2

            process(Opcode.ADD, "R1", "R2")
            assertThat(registers.r[0]).isEqualTo(3)

            process(Opcode.MULTIPLY, "R1", "R2")
            assertThat(registers.r[0]).isEqualTo(2)
        }
    }

    @Nested
    inner class LogicalOperation {

        @Test
        fun testLogicalOperation() {
            process(Opcode.CONDITION, "0x01", "0x02")
            assertThat(registers.r[0]).isEqualTo(1)

            process(Opcode.MOVE, "R2", "0x02")
            assertThat(registers.r[2]).isEqualTo(2)

            process(Opcode.JUMP, "0x03", "0x02")
            assertThat(registers.pc).isEqualTo(3)

            registers.r[0] = 1
            process(Opcode.BRANCH, "0x02", "0x01")
            assertThat(registers.pc).isEqualTo(2)
        }

        @Test
        fun operateWithRegister() {
            registers.r[0] = 1
            registers.r[1] = 1
            registers.r[2] = 2

            process(Opcode.JUMP, "R1", "0x02")
            assertThat(registers.pc).isEqualTo(1)

            process(Opcode.BRANCH, "R2", "0x01")
            assertThat(registers.pc).isEqualTo(2)
        }

        @DisplayName("MOVE의 첫번째 피연산자는 Register Number이어야만 한다")
        @Test
        fun invalidMoveOperand() {
            assertThrows<IllegalArgumentException> { process(Opcode.MOVE, "0x01", "0x02") }
        }
    }
}
