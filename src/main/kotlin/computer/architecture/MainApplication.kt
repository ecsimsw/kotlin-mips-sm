package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ForwardingPipelineControlUnit
import computer.architecture.cpu.pc.TwoLevelLocalHistoryPredictionPcUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    Logger.loggingSignal = loggingSignal

    val fileToLoad = "sample/gcd.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val controlUnit = ForwardingPipelineControlUnit(memory, TwoLevelLocalHistoryPredictionPcUnit())
    val processResult = controlUnit.process()

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
