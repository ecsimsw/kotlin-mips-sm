package computer.architecture.component

import computer.architecture.cpu.*

class Latches {

    private val IF_ID = Latch(FetchResult())
    private val ID_EX = Latch(DecodeResult())
    private val EX_MA = Latch(ExecutionResult())
    private val MA_WB = Latch(MemoryAccessResult())

    fun ifid(ifResult: FetchResult) {
        IF_ID.store(ifResult)
    }

    fun idex(idResult: DecodeResult) {
        ID_EX.store(idResult)
    }

    fun exma(exResult: ExecutionResult) {
        EX_MA.store(exResult)
    }

    fun mawb(maResult: MemoryAccessResult) {
        MA_WB.store(maResult)
    }

    fun ifid(): FetchResult {
        return IF_ID.fetch()
    }

    fun idex(): DecodeResult {
        return ID_EX.fetch()
    }

    fun exma(): ExecutionResult {
        return EX_MA.fetch()
    }

    fun mawb(): MemoryAccessResult {
        return MA_WB.fetch()
    }

    fun flushAll() {
        IF_ID.flush()
        ID_EX.flush()
        EX_MA.flush()
        MA_WB.flush()
    }
}
