package computer.architecture.cpu.pc

import computer.architecture.component.And.Companion.and
import computer.architecture.cpu.DecodeResult
import computer.architecture.cpu.ExecutionResult
import computer.architecture.cpu.FetchResult
import computer.architecture.cpu.bht.IHistoryRegister
import computer.architecture.cpu.bht.PatternHistoryRegister
import computer.architecture.cpu.prediction.BranchTargetBuffer
import computer.architecture.utils.Logger

open class HistoryBufferedBranchPredictionPcUnit(
    private val size: Int = 16,
    private val historyRegister: IHistoryRegister
) : DynamicBranchPredictionPcUnit() {

    private val branchTargetBuffer: BranchTargetBuffer = BranchTargetBuffer(size)
    private val patternHistoryRegister: PatternHistoryRegister = PatternHistoryRegister(size)

    override fun findNext(
        pc: Int,
        nextIfId: FetchResult,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {

        val btbHit = branchTargetBuffer.isHit(index(nextIfId.pc), (nextIfId.pc))
        val historyValue = historyRegister.valueOf(pc)
        val isTaken = patternHistoryRegister.pattern(historyValue).taken()

        if (and(btbHit, isTaken)) {
            Logger.predictTaken()
            return branchTargetBuffer.targetAddress(index(nextIfId.pc))
        }

        if (nextExMa.valid && nextExMa.controlSignal.branch) {
            var nextPc = pc + 4
            if (takenCorrect(nextExMa, nextIdEx)) {
                Logger.predictionSucceed()
            } else {
                nextPc = nextPc(nextExMa)
                nextIfId.valid = false
                nextIdEx.valid = false
                nextExMa.controlSignal.isEnd = nextPc == -1
                Logger.predictionFailed()
            }
            updateHistory(nextExMa.pc, nextExMa.nextPc, nextExMa.branch)
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

    private fun updateHistory(branchAddr: Int, targetAddr: Int, isTaken: Boolean) {
        val index = index(branchAddr)
        branchTargetBuffer.update(index, branchAddr, targetAddr)
        val historyValue = historyRegister.valueOf(branchAddr)
        patternHistoryRegister.pattern(historyValue).update(isTaken)
        historyRegister.update(branchAddr, isTaken)
    }

    private fun nextPc(nextExMa: ExecutionResult): Int {
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

    private fun index(pc: Int): Int {
        return (pc / 4) % size
    }
}
