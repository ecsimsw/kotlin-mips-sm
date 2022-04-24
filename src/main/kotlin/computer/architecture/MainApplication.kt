package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.Logger

fun main(args: Array<String>) {
    val logger = initLogger()
    println(args.size)

    val executionFiles = mutableListOf(*args)
    if (executionFiles.isEmpty()) {
        executionFiles.add("simple")
    }

    for (path in executionFiles) {
        val fileToLoad = "sample/$path.bin"
        val memory = Memory.load(20000000, fileToLoad)

        val controlUnit = ControlUnit(memory, logger)
        val processResult = controlUnit.process()

        logger.printProcessResult(processResult)
    }
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
