package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ControlUnit_Forwarding_BranchPrediction
import computer.architecture.cpu.cu.ControlUnit_Stall_Stall
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    val fileToLoad = "sample/simple2.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val logger = initLogger()
    val controlUnit = ControlUnit_Stall_Stall(memory, logger)
    val processResult = controlUnit.process()

    logger.printProcessResult(processResult)
}

private fun initLogger(): Logger {
    return Logger(loggingSignal)
}

val loggingSignal = LoggingSignal(
    cycle = true,
    cyclePrintPeriod = 1,
    fetch = true,
    decode = true,
    execute = true,
    memoryAccess = true,
    writeBack = true,
    result = true,
    sleepTime = 0,
    from = 0,
    to = 1999
)
