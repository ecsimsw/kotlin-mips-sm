package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.ICache
import computer.architecture.cpu.cache.WriteBackDirectMappedCache
import computer.architecture.cpu.cache.WriteThroughDirectMappedCache
import computer.architecture.cpu.cu.ForwardingPipelineControlUnit
import computer.architecture.cpu.pc.TwoLevelLocalHistoryPredictionPcUnit
import computer.architecture.utils.Logger
import computer.architecture.utils.LoggingSignal

fun main() {
    Logger.loggingSignal = loggingSignal

    val fileToLoad = "sample/input4.bin"
    val memory = Memory.load(20000000, fileToLoad)

    val cache = WriteBackDirectMappedCache(memory)
    val controlUnit = ForwardingPipelineControlUnit(cache, TwoLevelLocalHistoryPredictionPcUnit())
    val processResult = controlUnit.process()

    Logger.printProcessResult(processResult[0])
}

val loggingSignal = LoggingSignal(
    cycle = false,
    cyclePrintPeriod = 1,
    fetch = false,
    decode = false,
    execute = false,
    memoryAccess = false,
    writeBack = false,
    result = true,
    sleepTime = 0,
    from = 23373898,
    to = Int.MAX_VALUE
)
