package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.*
import computer.architecture.cpu.cu.ForwardingPipelineControlUnit
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.cpu.pc.TwoLevelLocalHistoryPredictionPcUnit
import computer.architecture.cpu.utils.Utils.Companion.checkProcessResult
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class CacheTest {

    @BeforeEach
    fun initLogger() {
        Logger.loggingSignal = LoggingSignal(result = true)
        Logger.init()
    }

    private val pcUnit = TwoLevelLocalHistoryPredictionPcUnit()

    @Disabled
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
    fun writeThroughDirectMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }

    @Disabled
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
    fun writeBackDirectMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackDirectMappedCache(memory, 4, 8)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }

    @Disabled
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
    fun writeThroughFullyAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughFullyAssociativeMappedCache(memory, 4, 8)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }

    @Disabled
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
    fun writeBackFullyAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackFullyAssociativeMappedCache(memory, 4, 8)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }

    @Disabled
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
    fun writeThrough2waySetAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughSetAssociativeMappedCache(memory, 4, 7, 1)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
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
    fun writeBack2WaySetAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackSetAssociativeMappedCache(memory, 4, 7, 1)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
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
    fun writeBack4WaySetAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackSetAssociativeMappedCache(memory, 4, 6, 2)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
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
    fun writeBack8WaySetAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackSetAssociativeMappedCache(memory, 4, 5, 3)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
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
    fun writeBack16WaySetAssociativeMappedCache(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteBackSetAssociativeMappedCache(memory, 4, 4, 4)

        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()
        checkProcessResult(processResult[0], expected)
    }
}
