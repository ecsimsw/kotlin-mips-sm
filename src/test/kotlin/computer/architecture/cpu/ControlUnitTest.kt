package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ControlUnit_Forwarding_BranchPrediction
import computer.architecture.cpu.cu.ControlUnit_Forwarding_Stall
import computer.architecture.cpu.cu.ControlUnit_SingleCycle
import computer.architecture.cpu.cu.ControlUnit_Stall_Stall
import computer.architecture.utils.Logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ControlUnitTest {

    private val logger = Logger.RESULT_ONLY

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
    fun singleCycle(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val controlUnit = ControlUnit_SingleCycle(memory, logger)
        val processResult = controlUnit.process()
        assertThat(processResult).isEqualTo(expected)
        logger.printProcessResult(processResult)
    }

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
    fun stall_stall(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val controlUnit = ControlUnit_Stall_Stall(memory, logger)
        val processResult = controlUnit.process()
        assertThat(processResult).isEqualTo(expected)
        logger.printProcessResult(processResult)
    }

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
    fun forwarding_stall(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val controlUnit = ControlUnit_Forwarding_Stall(memory, logger)
        val processResult = controlUnit.process()
        assertThat(processResult).isEqualTo(expected)
        logger.printProcessResult(processResult)
    }

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
    fun branchPrediction(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val controlUnit = ControlUnit_Forwarding_BranchPrediction(memory, logger)
        val processResult = controlUnit.process()
        assertThat(processResult).isEqualTo(expected)
        logger.printProcessResult(processResult)
    }
}
