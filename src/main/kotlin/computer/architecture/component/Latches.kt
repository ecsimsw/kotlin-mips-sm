package computer.architecture.component

import computer.architecture.cpu.dto.DecodeResult
import computer.architecture.cpu.dto.ExecutionResult
import computer.architecture.cpu.dto.FetchResult
import computer.architecture.cpu.dto.MemoryAccessResult

class Latches {

    private val ifId = Latch(FetchResult())
    private val idEx = Latch(DecodeResult())
    private val exMa = Latch(ExecutionResult())
    private val maWb = Latch(MemoryAccessResult())

    fun store(ifResult: FetchResult) = ifId.store(ifResult)
    fun store(idResult: DecodeResult) = idEx.store(idResult)
    fun store(exResult: ExecutionResult) = exMa.store(exResult)
    fun store(maResult: MemoryAccessResult) = maWb.store(maResult)

    fun ifId() = ifId.fetch()
    fun idEx() = idEx.fetch()
    fun exMa() = exMa.fetch()
    fun maWb() = maWb.fetch()

    fun flushAll() {
        ifId.flush()
        idEx.flush()
        exMa.flush()
        maWb.flush()
    }
}
