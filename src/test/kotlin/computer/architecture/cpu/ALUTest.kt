package computer.architecture.cpu

import computer.architecture.memory.Memory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

internal class ALUTest {

    private val memory = Memory(1000)
    private lateinit var registers: Registers
    private lateinit var alu: ALU

    @BeforeEach
    fun setUp() {
        registers = Registers(10)
        alu = ALU(registers, Memory(1000))
    }

    @Nested
    inner class SystemOperation {

        @DisplayName("HALT 명령어로 pc를 Int.MAX_VALUE로 한다.")
        @Test
        fun testHalt() {
            alu.process(Opcode.HALT, 0, 0)
            assertThat(registers.pc).isEqualTo(Int.MAX_VALUE)
        }

        @DisplayName("LW 명령어로 메모리 주소의 값을 읽는다")
        @Test
        fun testLW() {
            val address = 10
            val value = 16

            memory[address] = value
            alu = ALU(registers, memory)

            alu.process(Opcode.LOAD_WORD, address, value)
            assertThat(registers.r[0]).isEqualTo(value)
        }

        @DisplayName("SW 명령어로 메모리 주소에 값을 저장한다")
        @Test
        fun testSW() {
            val address = 10
            val value = 16

            alu = ALU(registers, memory)

            alu.process(Opcode.STORE_WORD, value, address)
            assertThat(Integer.decode(memory[address])).isEqualTo(value)
        }

        @DisplayName("JAL 명령어로 다음 PC를 지정하고, RA를 저장한다")
        @Test
        fun testJAL() {
            val currentPC = registers.pc
            val afterPC = 10
            val raRegister = 8

            alu = ALU(registers, memory)
            alu.process(Opcode.JUMP_AND_LINK, raRegister, afterPC)

            assertThat(registers.pc).isEqualTo(afterPC)
            assertThat(registers.r[raRegister]).isEqualTo(currentPC)
        }
    }

    @Nested
    inner class ArithmeticOperation {

        @DisplayName("사칙 연산을 수행한다.")
        @Test
        fun testArithmeticOperation() {
            alu.process(Opcode.ADD, 1, 2)
            assertThat(registers.r[0]).isEqualTo(3)

            alu.process(Opcode.MINUS, 2, 1)
            assertThat(registers.r[0]).isEqualTo(1)

            alu.process(Opcode.MULTIPLY, 1, 2)
            assertThat(registers.r[0]).isEqualTo(2)

            alu.process(Opcode.DIVIDE, 2, 1)
            assertThat(registers.r[0]).isEqualTo(2)
        }

        @DisplayName("MOD 연산으로 나머지를 계산한다.")
        @Test
        fun testMod() {
            alu.process(Opcode.MOD, 4, 3)
            assertThat(registers.r[0]).isEqualTo(1)
        }

        @DisplayName("비트 연산을 수행한다.")
        @Test
        fun testBitwiseOperation() {
            alu.process(Opcode.AND, 12, 25)
            assertThat(registers.r[0]).isEqualTo(8)

            alu.process(Opcode.OR, 12, 25)
            assertThat(registers.r[0]).isEqualTo(29)

            alu.process(Opcode.SLL, 1, 2)
            assertThat(registers.r[0]).isEqualTo(4)

            alu.process(Opcode.SRL, 4, 2)
            assertThat(registers.r[0]).isEqualTo(1)
        }
    }

    @Nested
    inner class LogicalOperation {

        @DisplayName("논리 연산을 수행한다.")
        @Test
        fun testLogicalOperation() {
            alu.process(Opcode.CONDITION, 1, 2)
            assertThat(registers.r[0]).isEqualTo(1)

            alu.process(Opcode.MOVE, 2, 2)
            assertThat(registers.r[2]).isEqualTo(2)

            alu.process(Opcode.JUMP, 3, 0)
            assertThat(registers.pc).isEqualTo(3)
        }

        @DisplayName("BRANCH 연산 : if R0 is 1, execute operand1 instruction")
        @Test
        fun testBranch() {
            registers.r[0] = 1
            alu.process(Opcode.BRANCH, 2, 0)
            assertThat(registers.pc).isEqualTo(2)
        }

        @DisplayName("BRANCH_ON_EQUAL : if R0 is operand1, execute operand2 instruction")
        @Test
        fun testBranchOnEqual() {
            registers.r[0] = 1
            alu.process(Opcode.BRANCH_ON_EQUAL, 1, 3)
            assertThat(registers.pc).isEqualTo(3)
        }

        @DisplayName("BRANCH_ON_NOT_EQUAL : if R0 is not operand1, execute operand2 instruction")
        @Test
        fun testBranchOnNotEqual() {
            registers.r[0] = 1
            alu.process(Opcode.BRANCH_ON_NOT_EQUAL, 2, 3)
            assertThat(registers.pc).isEqualTo(3)
        }
    }
}
