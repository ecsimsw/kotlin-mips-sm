package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.WriteThroughDirectMappedCache
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.cpu.utils.Utils.Companion.cache
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class MultiProcessingControlUnitTest {

    @BeforeEach
    fun initLogger() {
        Logger.loggingSignal = LoggingSignal(result = true)
        Logger.init()
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
    fun multiProcessing(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val caches = listOf(cache(memory), cache(memory), cache(memory), cache(memory), cache(memory))

        val controlUnit = MultiProcessingPipelineControlUnit(caches)
        val processResult = controlUnit.process()

        assertThat(processResult[0]).isEqualTo(expected)
        Logger.printProcessResult(processResult[0])
    }

    @Test
    fun multiProcessing_three() {
        val memory1 = Memory.load(20000000, "sample/simple4.bin")
        val memory2 = Memory.load(20000000, "sample/simple3.bin")
        val memory3 = Memory.load(20000000, "sample/fib.bin")

        val caches = listOf(cache(memory1), cache(memory2), cache(memory3))
        val controlUnit = MultiProcessingPipelineControlUnit(caches)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(listOf(55, 5050, 55))
    }

    @Test
    fun multiProcessing_six() {
        val memory1 = Memory.load(17000000, "sample/simple.bin")
        val memory2 = Memory.load(17000000, "sample/simple2.bin")
        val memory3 = Memory.load(17000000, "sample/simple3.bin")
        val memory4 = Memory.load(17000000, "sample/simple4.bin")
        val memory5 = Memory.load(17000000, "sample/gcd.bin")
        val memory6 = Memory.load(17000000, "sample/fib.bin")

        val caches = listOf(cache(memory1), cache(memory2), cache(memory3), cache(memory4), cache(memory5), cache(memory6))
        val controlUnit = MultiProcessingPipelineControlUnit(caches)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(listOf(0, 100, 5050, 55, 1, 55))
    }
}