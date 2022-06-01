package computer.architecture.cpu.prediction

import computer.architecture.cpu.bht.*
import kotlin.math.pow

open class TwoLevelBranchHistoryTable(
    private val size: Int = 16,
    private val historyBitSize:Int = 4
) : IBranchHistoryTable {
    private val branchHistoryTable = BranchHistoryTable(size)
    private val localHistoryTable = LocalHistoryTable(size, historyBitSize)
    private val patternHistoryTable = PatternHistoryTable(2.0.pow(historyBitSize).toInt())

    override fun isHit(pc : Int) : Boolean {
        return branchHistoryTable.isHit(index(pc), pc)
    }

    override fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = index(branchAddress)
        branchHistoryTable.update(index, branchAddress, target)
        val historyValue = localHistoryTable.historyValue(index)
        patternHistoryTable.pattern(historyValue).change(isTaken)
        localHistoryTable.update(index, isTaken)
    }

    override fun target(pc : Int) : Int {
        return branchHistoryTable.targetAddress(index(pc))
    }

    override fun state(pc: Int): IBitStateMachine {
        val historyValue = localHistoryTable.historyValue(index(pc))
        return patternHistoryTable.pattern(historyValue)
    }

    private fun index(pc : Int) : Int {
        return (pc / 4) % size
    }
}
