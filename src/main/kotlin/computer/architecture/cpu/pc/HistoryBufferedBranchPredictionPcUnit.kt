package computer.architecture.cpu.pc

import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult
import computer.architecture.cpu.bht.IBranchHistoryTable
import computer.architecture.cpu.prediction.TwoLevelBranchHistoryTable

class HistoryBufferedBranchPredictionPcUnit(
    private val historyBuffer : IBranchHistoryTable = TwoLevelBranchHistoryTable()
) : IProgramCounterUnit {

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if(historyBuffer.isHit(nextIfId.pc)) {
            if(historyBuffer.state(nextIfId.pc).taken()) {
                return historyBuffer.target(nextIfId.pc)
            }
        }

        if (nextExMa.valid && nextExMa.controlSignal.branch) {
            var nextPc = pc + 4
            if(!takenCorrect(nextExMa, nextIdEx)) {
                nextPc = nextPc(nextExMa)
                nextIfId.valid = false
                nextIdEx.valid = false
                nextExMa.controlSignal.isEnd = nextPc == -1
            } else {
                println("Correct")
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
