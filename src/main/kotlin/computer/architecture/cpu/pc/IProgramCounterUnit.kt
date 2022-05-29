package computer.architecture.cpu

interface IProgramCounterUnit {
    fun findNext(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): ProgramCounterResult
}

data class ProgramCounterResult(
    val isEnd : Boolean = false,
    val nextPc : Int =0
)
