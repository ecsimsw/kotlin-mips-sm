package computer.architecture.cpu.prediction

import computer.architecture.cpu.bht.BranchHistory
import computer.architecture.cpu.bht.IBranchHistoryTable
import computer.architecture.cpu.bht.LocalHistoryTable
import computer.architecture.cpu.bht.PatternHistoryTable

open class TwoLevelBranchHistoryTable(
    private val size: Int = 16,
) : IBranchHistoryTable {
    private val localHistoryTable = LocalHistoryTable(size)
    private val patternHistoryTable = PatternHistoryTable(size)

    private val histories = Array(size) {
        BranchHistory(-1,-1)
    }

    override fun isHit(pc : Int) : Boolean {
        return histories[index(pc)].branchAddress == pc
    }

    override fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = index(branchAddress)
        histories[index].branchAddress = branchAddress
        histories[index].targetAddress = target
        localHistoryTable.update(index, isTaken)
    }

    override fun target(pc : Int) : Int {
        return histories[index(pc)].targetAddress
    }

    override fun state(pc: Int): IBitStateMachine {
        val historyValue = localHistoryTable.historyValue(index(pc))
        return patternHistoryTable.pattern(historyValue)
    }

    private fun index(pc : Int) : Int {
        return (pc / 4) % size
    }
}
