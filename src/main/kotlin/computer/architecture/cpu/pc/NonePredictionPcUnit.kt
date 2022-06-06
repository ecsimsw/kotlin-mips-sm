package computer.architecture.cpu.pc

import computer.architecture.cpu.dto.DecodeResult
import computer.architecture.cpu.dto.ExecutionResult
import computer.architecture.cpu.dto.FetchResult

class NonePredictionPcUnit : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if (nextExMa.valid && nextExMa.branch) {
            nextIfId.valid = false
            nextIdEx.valid = false
            val nextPc = nextExMa.nextPc
            if (nextPc == -1) {
                nextExMa.controlSignal.isEnd = true
            }
            return nextPc
        }

        if (nextIdEx.valid && nextIdEx.jump) {
            nextIfId.valid = false
            val nextPc = nextIdEx.nextPc
            if (nextPc == -1) {
                nextIdEx.controlSignal.isEnd = true
            }
            return nextPc
        }
        return pc + 4
    }
}
