package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    val fileToLoad = "sample/simple3.bin"
    val memory1 = Memory.load(20000000, fileToLoad)
    val memory2 = Memory.load(20000000, fileToLoad)
    val memory3 = Memory.load(20000000, fileToLoad)
    val memory4 = Memory.load(20000000, fileToLoad)
    val memory5 = Memory.load(20000000, fileToLoad)

    val logger = initLogger()

    val controlUnit = MultiProcessingPipelineControlUnit(listOf(memory1, memory2, memory3, memory4, memory5), logger)
    val processResult = controlUnit.process()

    processResult.forEach { println(it) }

//    logger.printProcessResult(processResult[0])
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
