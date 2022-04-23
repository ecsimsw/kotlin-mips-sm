package computer.architecture.cpu

import computer.architecture.component.Memory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ControlUnitTest {

    @ParameterizedTest
    @CsvSource(
        "sample/simple.bin,0",
        "sample/simple2.bin,100",
        "sample/simple3.bin,5050",
        "sample/simple4.bin,55",
        "sample/gcd.bin,1",
        "sample/fib.bin,55",
        "sample/input4.bin,85"
    )
    fun process(path: String, expectedValue: Int) {
        val memory = Memory.load(20000000, path)
        val controlUnit = ControlUnit(memory)
        val processResult = controlUnit.process()
        assertThat(processResult).isEqualTo(expectedValue)
    }
}
