package computer.architecture.cpu.pc

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult
import computer.architecture.cpu.IProgramCounterUnit
import computer.architecture.cpu.prediction.AlwaysTakenStrategy
import computer.architecture.cpu.prediction.IBranchPredictionStrategy

class BranchPredictionUnit(
    private val branchPrediction: IBranchPredictionStrategy = AlwaysTakenStrategy()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): ProgramCounterResult {
        var nextPc = pc + 4

        if (predictionFailed(nextExMa, nextIfId)) {
            nextIfId.valid = false
            nextIdEx.valid = false

            nextPc = if(branchPrediction.taken(pc)) {
                nextExMa.pc + 4
            } else {
                nextExMa.nextPc
            }
            nextExMa.controlSignal.isEnd = nextPc == -1
        }

        if (taken(nextIdEx, pc)) {
            nextIfId.valid = false
            nextPc = nextIdEx.immediate
            nextIdEx.controlSignal.isEnd = nextPc == -1
        }

        if (jump(nextIdEx)) {
            nextIfId.valid = false
            nextPc = nextIdEx.nextPc
            nextIdEx.controlSignal.isEnd = nextPc == -1
        }
        return ProgramCounterResult(nextPc == -1, nextPc)
    }

    private fun jump(nextIdEx: DecodeResult) =
        nextIdEx.valid && nextIdEx.jump

    private fun taken(nextIdEx: DecodeResult, pc: Int) =
        nextIdEx.valid && nextIdEx.controlSignal.branch && branchPrediction.taken(pc)

    private fun predictionFailed(nextExMa: ExecutionResult, nextIfId: FetchResult) =
        nextExMa.valid && nextExMa.controlSignal.branch && !branchPrediction.isCorrect(nextIfId.pc, nextExMa.nextPc)
}