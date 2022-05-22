package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ControlUnit
import computer.architecture.cpu.cu.ControlUnitInterface
import computer.architecture.cpu.cu.ControlUnit_SingleCycle
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal
import computer.architecture.utils.PipeLineLogger

fun main() {
    val fileToLoad = "sample/simple.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val logger = initPipeLineLogger()
    val controlUnit: ControlUnitInterface = ControlUnit(memory, logger)
    val processResult = controlUnit.process()

    logger.printProcessResult(processResult)
}

val loggingSignal = LoggingSignal(
    cycle = true,
    cyclePrintPeriod = 1,
    fetch = true,
    decode = true,
    execute = true,
    memoryAccess = true,
    writeBack = true,
    resultInformation = true,
    sleepTime = 0,
    from = 0,
    to = Int.MAX_VALUE
)

private fun initLogger(): Logger {
    return Logger(loggingSignal)
}

private fun initPipeLineLogger(): Logger {
    return PipeLineLogger(loggingSignal)
}
