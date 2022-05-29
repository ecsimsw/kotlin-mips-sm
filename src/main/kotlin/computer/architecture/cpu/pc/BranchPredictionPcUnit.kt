package computer.architecture.cpu.pc

import computer.architecture.cpu.*
import computer.architecture.cpu.prediction.AlwaysTakenStrategy
import computer.architecture.cpu.prediction.IBranchPredictionStrategy

class BranchPredictionPcUnit(
    private val bpStrategy: IBranchPredictionStrategy = AlwaysTakenStrategy()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): ProgramCounterResult {
        var nextPc = pc + 4

        if (nextExMa.valid && nextExMa.controlSignal.branch && !takenCorrect(nextExMa, nextIfId)) {
            nextIfId.valid = false
            nextIdEx.valid = false
            nextPc = bpStrategy.predict(nextExMa)
            nextExMa.controlSignal.isEnd = nextPc == -1
        }

        if (nextIdEx.valid && isTaken(nextIdEx, pc)) {
            nextIfId.valid = false
            nextPc = nextIdEx.immediate
            nextIdEx.controlSignal.isEnd = nextPc == -1
        }

        if (nextIdEx.valid && jump(nextIdEx)) {
            nextIfId.valid = false
            nextPc = nextIdEx.nextPc
            nextIdEx.controlSignal.isEnd = nextPc == -1
        }
        return ProgramCounterResult(nextPc == -1, nextPc)
    }

    private fun takenCorrect(nextExMa: ExecutionResult, nextIfId: FetchResult): Boolean {
        val wasTaken = nextIfId.pc == nextExMa.nextPc
        return nextExMa.branch == wasTaken
    }

    private fun isTaken(nextIdEx: DecodeResult, pc: Int): Boolean {
        return nextIdEx.controlSignal.branch && bpStrategy.taken(pc)
    }

    private fun jump(nextIdEx: DecodeResult): Boolean {
        return nextIdEx.jump
    }
}
