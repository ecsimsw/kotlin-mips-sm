package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.bht.GlobalHistoryRegister
import computer.architecture.cpu.bht.LocalHistoryRegister
import computer.architecture.cpu.cu.ForwardingPipeLineControlUnit
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.cpu.cu.SingleCycleControlUnit
import computer.architecture.cpu.cu.StallingPipeLineControlUnit
import computer.architecture.cpu.pc.*
import computer.architecture.cpu.prediction.*
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

internal class ControlUnitTest {

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

        val controlUnit = SingleCycleControlUnit(memory)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val pcUnit = NonePredictionPcUnit()
        val controlUnit = StallingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = StallingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = StallingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val pcUnit = NonePredictionPcUnit()
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val predictionStrategy = BTFNTStrategy()
        val pcUnit = StaticBranchPredictionPcUnit(predictionStrategy)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val bitState = SingleBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val bitState = SaturationTwoBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val bitState = HysteresisTwoBitStateMachine()
        val pcUnit = DynamicBranchPredictionPcUnit(bitState)
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val pcUnit = TwoLevelGlobalHistoryPredictionPcUnit()
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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

        val pcUnit = TwoLevelLocalHistoryPredictionPcUnit()
        val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        Logger.printProcessResult(processResult)
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
        val memory1 = Memory.load(20000000, path)
        val memory2 = Memory.load(20000000, path)
        val memory3 = Memory.load(20000000, path)
        val memory4 = Memory.load(20000000, path)
        val memory5 = Memory.load(20000000, path)

        val controlUnit =
            MultiProcessingPipelineControlUnit(listOf(memory1, memory2, memory3, memory4, memory5))
        val processResult = controlUnit.process()

        assertThat(processResult[0]).isEqualTo(expected)
        Logger.printProcessResult(processResult[0])
    }
}
