package computer.architecture.cpu.prediction

import computer.architecture.cpu.bht.*

open class OneLevelBranchHistoryTable(
    private val size: Int = 16,
) : IBranchHistoryTable {
    private val branchHistoryTable = BranchHistoryTable(size)
    private val patternHistoryTable = PatternHistoryTable(size)

    override fun isHit(pc : Int) : Boolean {
        return branchHistoryTable.isHit(index(pc), pc)
    }

    override fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = index(branchAddress)
        branchHistoryTable.update(index, branchAddress, target)
        patternHistoryTable.update(index, isTaken)
    }

    override fun target(pc : Int) : Int {
        return branchHistoryTable.targetAddress(index(pc))
    }

    override fun state(pc: Int): IBitStateMachine {
        return patternHistoryTable.pattern(index(pc))
    }

    private fun index(pc : Int) : Int {
        return (pc / 4) % size
    }
}
