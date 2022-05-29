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

//        println(nextExMa.valid)
//        println(nextExMa.controlSignal.branch)
//        println(nextExMa.branch)
//        println(nextIfId.pc)
//        println(nextExMa.nextPc)

        if (predictionFailed(nextExMa)) {
            nextIfId.valid = false
            nextIdEx.valid = false
            nextPc = bpStrategy.predict(nextExMa)
            nextExMa.controlSignal.isEnd = nextPc == -1
        }

        if (predictAndTake(nextIdEx, pc)) {
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

    private fun predictionFailed(nextExMa: ExecutionResult): Boolean {
        return nextExMa.valid
                && nextExMa.controlSignal.branch
                && nextExMa.branch != bpStrategy.taken(0)
    }

    private fun predictAndTake(nextIdEx: DecodeResult, pc: Int): Boolean {
        return nextIdEx.valid
                && nextIdEx.controlSignal.branch
                && bpStrategy.taken(pc)
    }

    private fun jump(nextIdEx: DecodeResult): Boolean {
        return nextIdEx.valid
                && nextIdEx.jump
    }
}
