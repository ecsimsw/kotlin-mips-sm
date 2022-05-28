package computer.architecture.cpu

import computer.architecture.cpu.pc.ProgramCounterResult

interface IProgramCounterUnit {
    fun findNext(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): ProgramCounterResult
}
