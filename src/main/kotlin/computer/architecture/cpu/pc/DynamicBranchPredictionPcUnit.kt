package computer.architecture.cpu.pc

import computer.architecture.cpu.dto.DecodeResult
import computer.architecture.cpu.dto.ExecutionResult
import computer.architecture.cpu.dto.FetchResult
import computer.architecture.cpu.prediction.IBitStateMachine
import computer.architecture.cpu.prediction.SaturationTwoBitStateMachine
import computer.architecture.utils.Logger

open class DynamicBranchPredictionPcUnit(
    private val state: IBitStateMachine = SaturationTwoBitStateMachine()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if (nextExMa.valid && nextExMa.controlSignal.branch) {
            if (takenCorrect(nextExMa, nextIfId)) {
                state.update(false)
                Logger.predictionSucceed()
            } else {
                nextIfId.valid = false
                nextIdEx.valid = false
                val nextPc = nextPc(nextExMa)
                state.update(true)
                nextExMa.controlSignal.isEnd = nextPc == -1
                Logger.predictionFailed()
                return nextPc
            }
        }

        if (nextIdEx.valid && isTaken(nextIdEx)) {
            nextIfId.valid = false
            val nextPc = nextIdEx.immediate
            nextIdEx.controlSignal.isEnd = nextPc == -1
            Logger.predictTaken()
            return nextPc
        }

        if (nextIdEx.valid && jump(nextIdEx)) {
            nextIfId.valid = false
            val nextPc = nextIdEx.nextPc
            nextIdEx.controlSignal.isEnd = nextPc == -1
            return nextPc
        }
        return pc + 4
    }

    private fun nextPc(nextExMa: ExecutionResult): Int {
        return if (nextExMa.branch) {
            nextExMa.nextPc
        } else {
            nextExMa.pc + 8
        }
    }

    private fun takenCorrect(nextExMa: ExecutionResult, nextIfId: FetchResult): Boolean {
        val wasTaken = nextIfId.pc == nextExMa.nextPc
        return nextExMa.branch == wasTaken
    }

    private fun isTaken(nextIdEx: DecodeResult): Boolean {
        return nextIdEx.controlSignal.branch && state.taken()
    }

    private fun jump(nextIdEx: DecodeResult): Boolean {
        return nextIdEx.jump
    }
}
