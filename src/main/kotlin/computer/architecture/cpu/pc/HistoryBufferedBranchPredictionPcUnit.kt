package computer.architecture.cpu.pc

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult
import computer.architecture.cpu.prediction.BranchHistoryTable

class HistoryBufferedBranchPredictionPcUnit(
    private val historyBuffer : BranchHistoryTable = BranchHistoryTable()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if(historyBuffer.isHit(nextIfId.pc)) {
            if(historyBuffer.state(nextIfId.pc).taken()) {
                println("taken")
                return historyBuffer.target(nextIfId.pc)
            }
        }

        if (nextExMa.valid && nextExMa.controlSignal.branch) {
            val nextPc = nextPc(nextExMa)
            if(!takenCorrect(nextExMa, nextIdEx)) {
                nextIfId.valid = false
                nextIdEx.valid = false
                nextExMa.controlSignal.isEnd = nextPc == -1
                println("wrong")
            } else {
                println("correct!")
            }
            historyBuffer.update(nextExMa.pc, nextExMa.nextPc, nextExMa.branch)
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
        return if (nextExMa.branch) {
            nextExMa.nextPc
        } else {
            nextExMa.pc + 4
        }
    }

    private fun takenCorrect(nextExMa: ExecutionResult, nextIdEx: DecodeResult): Boolean {
        val wasTaken = nextIdEx.pc == nextExMa.nextPc
        return nextExMa.branch == wasTaken
    }

    private fun jump(nextIdEx: DecodeResult): Boolean {
        return nextIdEx.jump
    }
}
