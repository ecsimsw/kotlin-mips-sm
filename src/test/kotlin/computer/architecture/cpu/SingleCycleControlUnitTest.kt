package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.WriteBackDirectMappedCache
import computer.architecture.cpu.cache.WriteThroughDirectMappedCache
import computer.architecture.cpu.cu.SingleCycleControlUnit
import computer.architecture.cpu.utils.Utils.Companion.checkProcessResult
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class SingleCycleControlUnitTest {

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
    fun singleCycle(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val controlUnit = SingleCycleControlUnit(cache)
        val processResult = controlUnit.process()

        checkProcessResult(processResult[0], expected)
    }
}
