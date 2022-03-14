package computer.architecture.cpu

import computer.architecture.memory.Memory
import computer.architecture.memory.Results
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File

@ExtendWith(MockKExtension::class)
internal class ControlUnitTest {

    @MockK
    private lateinit var mockResults: Results

    @BeforeEach
    private fun setUp() {
        every { mockResults.log(any(), any()) } returns Unit
    }

    @DisplayName("input/GCD_Recursive 를 파라미터 테스트한다.")
    @Test
    fun testGCD_recursive() {
        val op1 = 25
        val op2 = 30
        val instructions = instructions("input/GCD_Recursive.txt", op1, op2)

        val cu = ControlUnit(Memory(instructions), mockResults)
        cu.process()

        val expected = TestUtils.gcd(op1, op2)
        assertThat(cu.registers(0)).isEqualTo(expected)
    }

    @DisplayName("input/GCD_Loop 를 파라미터 테스트한다.")
    @Test
    fun testGCD_loop() {
        val op1 = 34
        val op2 = 30
        val instructions = instructions("input/GCD_Loop.txt", op1, op2)

        val cu = ControlUnit(Memory(instructions), mockResults)
        cu.process()

        val expected = TestUtils.gcd(op1, op2)
        assertThat(cu.registers(0)).isEqualTo(expected)
    }

    @DisplayName("input/LCM 를 파라미터 테스트한다.")
    @Test
    fun testLCM() {
        val op1 = 28
        val op2 = 30
        val instructions = instructions("input/LCM.txt", op1, op2)

        val cu = ControlUnit(Memory(instructions), mockResults)
        cu.process()

        val expected = TestUtils.lcm(op1, op2)
        assertThat(cu.registers(0)).isEqualTo(expected)
    }

    private fun instructions(filePath : String, vararg args : Int): MutableList<String> {
        val instructions: MutableList<String> = arrayListOf();
        File(filePath).forEachLine { instructions.add(it) }

        for(index:Int in args.indices) {
            instructions.removeAt(index)
            val operand = Integer.toHexString(args[index])
            instructions.add(index, "M R${index+1} 0x${operand}")
        }
        return instructions
    }
}
