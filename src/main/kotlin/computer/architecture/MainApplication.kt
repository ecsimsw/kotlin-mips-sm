package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ControlUnit
import computer.architecture.cpu.cu.ControlUnitInterface
import computer.architecture.cpu.cu.ControlUnit_SingleCycle
import computer.architecture.utils.Logger

fun main() {
    val logger = initLogger()
    val fileToLoad = "sample/simple.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val controlUnit: ControlUnitInterface = ControlUnit(memory, logger)
    val processResult = controlUnit.process()

    logger.printProcessResult(processResult)
}

private fun initLogger(): Logger {
    return Logger.init(
        cycle = true,
        fetch = true,
        decode = true,
        execute = true,
        memoryAccess = true,
        writeBack = true,
        resultInformation = true,
        sleepTime = 0
    )
}
