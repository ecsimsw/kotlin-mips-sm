package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    initLoggingSignal()

    val logger = Logger()
    val fileToLoad = "test_prog/input4.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val controlUnit = ControlUnit(memory, logger)
    val processResult = controlUnit.process()

    logger.printProcessResult(processResult)
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        cycle = true,
        fetch = false,
        decode = false,
        execute = false,
        memoryAccess = false,
        writeBack = false,
        resultInformation = true,
        sleepTime = 0
    )
}
