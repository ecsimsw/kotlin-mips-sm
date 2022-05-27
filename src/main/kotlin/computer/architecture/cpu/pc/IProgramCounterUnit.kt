package computer.architecture.cpu

import computer.architecture.cpu.pc.ProgramCounterResult

interface IProgramCounterUnit {
    fun execute(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): ProgramCounterResult
}
