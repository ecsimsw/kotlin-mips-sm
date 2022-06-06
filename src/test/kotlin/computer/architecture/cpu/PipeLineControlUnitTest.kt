package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.WriteThroughDirectMappedCache
import computer.architecture.cpu.cu.ForwardingPipelineControlUnit
import computer.architecture.cpu.cu.SingleCycleControlUnit
import computer.architecture.cpu.cu.StallingPipelineControlUnit
import computer.architecture.cpu.pc.*
import computer.architecture.cpu.prediction.*
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class PipeLineControlUnitTest {

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
        val cache = WriteThroughDirectMappedCache(memory)
        val pcUnit = NonePredictionPcUnit()
        val controlUnit = StallingPipelineControlUnit(cache, pcUnit)
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
    fun stall_alwaysNotTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = StallingPipelineControlUnit(cache, pcUnit)
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
    fun stall_alwaysTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = StallingPipelineControlUnit(cache, pcUnit)
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
    fun forwarding_stall(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val pcUnit = NonePredictionPcUnit()
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
    fun forwarding_alwaysTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
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
    fun forwarding_alwaysNotTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
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
    fun forwarding_btfnt(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val predictionStrategy = BTFNTStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
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
    fun forwarding_1bitPrediction(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val bitState = SingleBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
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
    fun forwarding_saturation2bitPrediction(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val bitState = SaturationTwoBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
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
    fun forwarding_hysteresis2bitPrediction(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val bitState = HysteresisTwoBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
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
    fun forwarding_2LevelGlobalHistoryBuffered(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val pcUnit = TwoLevelGlobalHistoryPredictionPcUnit()
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
    fun forwarding_2LevelLocalHistoryBuffered(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)
        val cache = WriteThroughDirectMappedCache(memory)
        val pcUnit = TwoLevelLocalHistoryPredictionPcUnit()
        val controlUnit = ForwardingPipelineControlUnit(cache, pcUnit)
        val processResult = controlUnit.process()

        checkProcessResult(processResult[0], expected)
    }

    private fun checkProcessResult(processResult: Int, expected: Int) {
        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
    }
}
