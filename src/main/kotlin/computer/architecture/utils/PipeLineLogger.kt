package computer.architecture.utils

import computer.architecture.cpu.*

class PipeLineLogger(loggingSignal: LoggingSignal) : Logger(loggingSignal) {
    private val cycleLogs = Array(5) { CycleLog() }

    override fun log(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        cycleLogs[0].fetchResult = fetchResult
        cycleLogs[1].decodeResult = decodeResult
        cycleLogs[2].executionResult = executionResult
        cycleLogs[3].memoryAccessResult = memoryAccessResult
        cycleLogs[4].writeBackResult = writeBackResult

        collect(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
        printCycleLog(
            fetchResult = cycleLogs[4].fetchResult,
            decodeResult = cycleLogs[4].decodeResult,
            executionResult = cycleLogs[4].executionResult,
            memoryAccessResult = cycleLogs[4].memoryAccessResult,
            writeBackResult = cycleLogs[4].writeBackResult
        )
        flushCycleLog()
    }

    override fun printCycleLog(
        fetchResult: FetchResult,
        decodeResult: DecodeResult,
        executionResult: ExecutionResult,
        memoryAccessResult: MemoryAccessResult,
        writeBackResult: WriteBackResult
    ) {
        if (
            fetchResult.pc == decodeResult.pc
            && decodeResult.pc == executionResult.pc
            && executionResult.pc == memoryAccessResult.pc
            && memoryAccessResult.pc == writeBackResult.pc
            && fetchResult.valid
            && decodeResult.valid
            && executionResult.valid
            && memoryAccessResult.valid
            && writeBackResult.valid
        ) {
            super.printCycleLog(fetchResult, decodeResult, executionResult, memoryAccessResult, writeBackResult)
        }
    }

    private fun flushCycleLog() {
        cycleLogs[4] = cycleLogs[3]
        cycleLogs[3] = cycleLogs[2]
        cycleLogs[2] = cycleLogs[1]
        cycleLogs[1] = cycleLogs[0]
        cycleLogs[0] = CycleLog()
    }
}

data class CycleLog(
    var fetchResult: FetchResult = FetchResult(),
    var decodeResult: DecodeResult = DecodeResult(),
    var executionResult: ExecutionResult = ExecutionResult(),
    var memoryAccessResult: MemoryAccessResult = MemoryAccessResult(),
    var writeBackResult: WriteBackResult = WriteBackResult(),
)
