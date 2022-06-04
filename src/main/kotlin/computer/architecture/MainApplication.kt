package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.MultiProcessingPipelineControlUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    Logger.loggingSignal = loggingSignal

    val memory1 = Memory.load(20000000, "sample/simple.bin")
    val memory2 = Memory.load(20000000, "sample/simple2.bin")
    val memory3 = Memory.load(20000000, "sample/simple3.bin")
    val memory4 = Memory.load(20000000, "sample/simple4.bin")
    val memory5 = Memory.load(20000000, "sample/gcd.bin")
    val memory6 = Memory.load(20000000, "sample/fib.bin")

    val controlUnit = MultiProcessingPipelineControlUnit(listOf(memory1, memory2, memory3, memory4, memory5, memory6))
    val processResult = controlUnit.process()

    processResult.forEach { println(it) }

    Logger.printProcessResult(processResult[0])
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
    to = Int.MAX_VALUE
)
