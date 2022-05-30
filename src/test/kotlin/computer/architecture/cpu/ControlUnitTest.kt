package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.FPipeLineControlUnit
import computer.architecture.cpu.cu.SPipeLineControlUnit
import computer.architecture.cpu.cu.SingleCycleControlUnit
import computer.architecture.cpu.pc.BranchPredictionPcUnit
import computer.architecture.cpu.prediction.AlwaysNotTakenStrategy
import computer.architecture.cpu.prediction.AlwaysTakenStrategy
import computer.architecture.cpu.prediction.BTFNT
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

        val controlUnit = SingleCycleControlUnit(memory, logger)
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

        val pcUnit = StallingPcUnit()
        val controlUnit = SPipeLineControlUnit(memory, logger, pcUnit)
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
    fun stall_alwaysNotTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)

        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = BranchPredictionPcUnit(predictionStrategy)
        val controlUnit = SPipeLineControlUnit(memory, logger, pcUnit)
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
    fun stall_alwaysTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)

        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = BranchPredictionPcUnit(predictionStrategy)
        val controlUnit = SPipeLineControlUnit(memory, logger, pcUnit)
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

        val pcUnit = StallingPcUnit()
        val controlUnit = FPipeLineControlUnit(memory, logger, pcUnit)
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
    fun forwarding_alwaysTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)

        val predictionStrategy = AlwaysTakenStrategy()
        val pcUnit = BranchPredictionPcUnit(predictionStrategy)
        val controlUnit = FPipeLineControlUnit(memory, logger, pcUnit)
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
    fun forwarding_alwaysNotTaken(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)

        val predictionStrategy = AlwaysNotTakenStrategy()
        val pcUnit = BranchPredictionPcUnit(predictionStrategy)
        val controlUnit = FPipeLineControlUnit(memory, logger, pcUnit)
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
    fun forwarding_btfnt(path: String, expected: Int) {
        val memory = Memory.load(20000000, path)

        val predictionStrategy = BTFNT()
        val pcUnit = BranchPredictionPcUnit(predictionStrategy)
        val controlUnit = FPipeLineControlUnit(memory, logger, pcUnit)
        val processResult = controlUnit.process()

        assertThat(processResult).isEqualTo(expected)
        logger.printProcessResult(processResult)
    }
}
