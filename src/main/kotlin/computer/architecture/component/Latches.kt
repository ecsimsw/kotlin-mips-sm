package computer.architecture.component

import computer.architecture.cpu.*

class Latches {

    private val ifId = Latch(FetchResult())
    private val idEx = Latch(DecodeResult())
    private val exMa = Latch(ExecutionResult())
    private val maWb = Latch(MemoryAccessResult())

    fun store(ifResult: FetchResult) {
        ifId.store(ifResult)
    }

    fun store(idResult: DecodeResult) {
        idEx.store(idResult)
    }

    fun store(exResult: ExecutionResult) {
        exMa.store(exResult)
    }

    fun store(maResult: MemoryAccessResult) {
        maWb.store(maResult)
    }

    fun ifId(): FetchResult {
        return ifId.fetch()
    }

    fun idEx(): DecodeResult {
        return idEx.fetch()
    }

    fun exMa(): ExecutionResult {
        return exMa.fetch()
    }

    fun maWb(): MemoryAccessResult {
        return maWb.fetch()
    }

    fun flushAll() {
        ifId.flush()
        idEx.flush()
        exMa.flush()
        maWb.flush()
    }
}
