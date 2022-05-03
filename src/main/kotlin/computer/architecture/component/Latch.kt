package computer.architecture.component

import computer.architecture.cpu.*

class Latch {

    private val fetchResults = Array(2) { FetchResult(0) }
    private val decodeResults = Array(2) { DecodeResult() }
    private val executionResults = Array(2) { ExecutionResult() }
    private val memoryAccessResults = Array(2) { MemoryAccessResult() }
    private val writeBackResults = Array(2) { WriteBackResult() }

    fun pushFetchResult(fetchResult: FetchResult) {
        fetchResults[0] = fetchResult
    }

    fun popFetchResult(): FetchResult {
        return fetchResults[1]
    }

    fun pushDecodeResult(decodeResult: DecodeResult) {
        decodeResults[0] = decodeResult
    }

    fun popDecodeResult(): DecodeResult {
        return decodeResults[1]
    }

    fun pushExecutionResult(executionResult: ExecutionResult) {
        executionResults[0] = executionResult
    }

    fun popExecutionResult(): ExecutionResult {
        return executionResults[1]
    }

    fun pushMemoryAccessResult(memoryAccessResult: MemoryAccessResult) {
        memoryAccessResults[0] = memoryAccessResult
    }

    fun popMemoryAccessResult(): MemoryAccessResult {
        return memoryAccessResults[1]
    }

    fun pushWriteBackResult(writeBackResult: WriteBackResult) {
        writeBackResults[0] = writeBackResult
    }

    fun flush() {
        fetchResults[1] = fetchResults[0]
        decodeResults[1] = decodeResults[0]
        executionResults[1] = executionResults[0]
        memoryAccessResults[1] = memoryAccessResults[0]
        writeBackResults[1] = writeBackResults[0]
    }
}
