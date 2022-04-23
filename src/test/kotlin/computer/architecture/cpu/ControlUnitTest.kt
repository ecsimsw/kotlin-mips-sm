package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.utils.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ControlUnitTest {

    private lateinit var memory: Memory

    @BeforeEach
    private fun setMemory() {
        memory = Memory(20000000)
    }

    @DisplayName("simple.bin")
    @ParameterizedTest
    @CsvSource(
        "test_prog/simple.bin,0",
        "test_prog/simple2.bin,100",
        "test_prog/simple3.bin,5050",
        "test_prog/simple4.bin,55",
        "test_prog/gcd.bin,1",
        "test_prog/fib.bin,55",
        "test_prog/input4.bin,85"
    )
    fun process(path: String, expectedValue: Int) {
        memory.loadFile(path)

        val controlUnit = ControlUnit(memory, Logger())
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expectedValue)
    }
}