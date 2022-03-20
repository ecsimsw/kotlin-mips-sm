package computer.architecture.cpu

import computer.architecture.memory.Memory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

internal class ALUTest {

    private lateinit var registers: Registers
    private lateinit var alu: ALU

    @BeforeEach
    fun setUp() {
        registers = Registers()
        alu = ALU(registers, Memory(1000))
    }

    private fun process(opcode: Opcode, operand1: String, operand2: String) {
        alu.process(opcode, Operand(operand1), Operand(operand2))
    }

    @Nested
    inner class SystemOperation {

        @DisplayName("HALT 명령어로 pc를 Int.MAX_VALUE로 한다.")
        @Test
        fun testHalt() {
            process(Opcode.HALT, "0x02", "0x02")
            assertThat(registers.pc).isEqualTo(Int.MAX_VALUE)
        }

        @DisplayName("LW 명령어로 메모리 주소의 값을 읽는다")
        @Test
        fun testLW() {
            val memory = Memory(1000)
            val address = "0x0A"
            val value = "0x1A"

            memory[Integer.decode(address)] = value
            alu = ALU(registers, memory)

            process(Opcode.LOAD_WORD, address, "0x00")
            assertThat(registers.r[0]).isEqualTo(Integer.decode(value))
        }

        @DisplayName("SW 명령어로 메모리 주소에 값을 저장한다")
        @Test
        fun testSW() {
            val memory = Memory(1000)
            val address = "0x0A"
            val value = "0x1A"

            alu = ALU(registers, memory)

            process(Opcode.STORE_WORD, value, address)
            assertThat(memory[Integer.decode(address)]).isEqualTo(value)
        }

        @DisplayName("레지스터 값으로 LW, SW 명령을 수행할 수 있다.")
        @Test
        fun testLoadAndStoreWithRegs() {
            val registers = Registers()
            val memory = Memory(1000)
            alu = ALU(registers, memory)

            registers.r[0] = 1
            registers.r[4] = 26
            process(Opcode.STORE_WORD, "R4", "R0")
            assertThat(memory[1]).isEqualTo("0x1A")

            registers.r[2] = 1
            process(Opcode.LOAD_WORD, "R2", "0x00")
            assertThat(registers.r[0]).isEqualTo(26)
        }
    }

    @Nested
    inner class ArithmeticOperation {

        @DisplayName("사칙 연산을 수행한다.")
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
        }

        @DisplayName("MOD 연산으로 나머지를 계산한다.")
        @Test
        fun testMod() {
            process(Opcode.MOD, "0x04", "0x03")
            assertThat(registers.r[0]).isEqualTo(1)
        }

        @DisplayName("비트 연산을 수행한다.")
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

        @DisplayName("레지스터 값으로도 산술 연산을 수행할 수 있다.")
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

        @DisplayName("논리 연산을 수행한다.")
        @Test
        fun testLogicalOperation() {
            process(Opcode.CONDITION, "0x01", "0x02")
            assertThat(registers.r[0]).isEqualTo(1)

            process(Opcode.MOVE, "R2", "0x02")
            assertThat(registers.r[2]).isEqualTo(2)

            process(Opcode.JUMP, "0x03", "0x00")
            assertThat(registers.pc).isEqualTo(3)
        }

        @DisplayName("BRANCH 연산 : if R0 is 1, execute operand1 instruction")
        @Test
        fun testBranch() {
            registers.r[0] = 1
            process(Opcode.BRANCH, "0x02", "0x00")
            assertThat(registers.pc).isEqualTo(2)
        }

        @DisplayName("BRANCH_ON_EQUAL : if R0 is operand1, execute operand2 instruction")
        @Test
        fun testBranchOnEqual() {
            registers.r[0] = 1
            process(Opcode.BRANCH_ON_EQUAL, "0x01", "0x03")
            assertThat(registers.pc).isEqualTo(3)
        }

        @DisplayName("BRANCH_ON_NOT_EQUAL : if R0 is not operand1, execute operand2 instruction")
        @Test
        fun testBranchOnNotEqual() {
            registers.r[0] = 1
            process(Opcode.BRANCH_ON_NOT_EQUAL, "0x02", "0x03")
            assertThat(registers.pc).isEqualTo(3)
        }

        @DisplayName("레지스터 값으로도 논리 연산을 수행할 수 있다.")
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
