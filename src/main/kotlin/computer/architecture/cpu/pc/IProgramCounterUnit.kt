package computer.architecture.cpu.pc

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult

interface IProgramCounterUnit {

    fun findNext(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): Int
}
