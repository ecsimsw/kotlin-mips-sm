package computer.architecture.cpu

interface IProgramCounterUnit {
    fun findNext(pc: Int, nextIfId: FetchResult, nextIdEx: DecodeResult, nextExMa: ExecutionResult): Int
}
