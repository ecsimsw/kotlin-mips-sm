package computer.architecture.cpu.pc

import computer.architecture.cpu.dto.DecodeResult
import computer.architecture.cpu.dto.ExecutionResult
import computer.architecture.cpu.dto.FetchResult

interface IProgramCounterUnit {

    fun findNext(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): Int
}
