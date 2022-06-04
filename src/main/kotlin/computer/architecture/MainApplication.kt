package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cu.ForwardingPipeLineControlUnit
import computer.architecture.cpu.pc.TwoLevelGlobalHistoryPredictionPcUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    Logger.loggingSignal = loggingSignal

    val fileToLoad = "sample/simple3.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val pcUnit = TwoLevelGlobalHistoryPredictionPcUnit()
    val controlUnit = ForwardingPipeLineControlUnit(memory, pcUnit)
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
    to = 1999
)
