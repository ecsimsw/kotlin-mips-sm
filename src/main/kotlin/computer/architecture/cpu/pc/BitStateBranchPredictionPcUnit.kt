package computer.architecture.cpu.pc

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult
import computer.architecture.cpu.prediction.BitStateMachine
import computer.architecture.cpu.prediction.SaturationTwoBitState

class BitStateBranchPredictionPcUnit(
    val state: BitStateMachine = SaturationTwoBitState()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if (nextExMa.valid && nextExMa.controlSignal.branch) {
            if(takenCorrect(nextExMa, nextIfId)) {
                state.change(false)
            } else {
                nextIfId.valid = false
                nextIdEx.valid = false
                val nextPc = nextPc(nextExMa)
                state.change(true)
                nextExMa.controlSignal.isEnd = nextPc == -1
                return nextPc
            }
        }

        if (nextIdEx.valid && isTaken(nextIdEx)) {
            nextIfId.valid = false
            val nextPc = nextIdEx.immediate
            nextIdEx.controlSignal.isEnd = nextPc == -1
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

    private fun nextPc(nextExMa: ExecutionResult) : Int {
        return if (state.taken()) {
            nextExMa.pc + 4
        } else {
            nextExMa.nextPc
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
