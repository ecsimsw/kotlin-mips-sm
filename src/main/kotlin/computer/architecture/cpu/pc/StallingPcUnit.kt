package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.pc.ProgramCounterResult

class StallingPcUnit : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): ProgramCounterResult {

        var isEnd = false
        if (nextExMa.valid && nextExMa.branch) {
            nextIfId.valid = false
            nextIdEx.valid = false
            if (nextExMa.nextPc == -1) {
                nextExMa.controlSignal.isEnd = true
                isEnd = true
            }
        }

        if (nextIdEx.valid && nextIdEx.jump) {
            nextIfId.valid = false
            if (nextIdEx.nextPc == -1) {
                nextIdEx.controlSignal.isEnd = true
                isEnd = true
            }
        }

        var nextPc = mux(nextExMa.branch, nextExMa.nextPc, pc + 4)
        nextPc = mux(nextIdEx.jump, nextIdEx.nextPc, nextPc)

        return ProgramCounterResult(isEnd, nextPc)
    }
}
