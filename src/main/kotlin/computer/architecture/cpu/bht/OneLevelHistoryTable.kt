package computer.architecture.cpu.bht

import computer.architecture.cpu.prediction.IBitStateMachine

open class OneLevelHistoryTable(
    private val size: Int = 16,
) : IHistoryTable {
    private val branchTargetBuffer = BranchTargetBuffer(size)
    private val patternHistoryTable = PatternHistoryTable(size)

    override fun isHit(pc : Int) : Boolean {
        return branchTargetBuffer.isHit(index(pc), pc)
    }

    override fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = index(branchAddress)
        branchTargetBuffer.update(index, branchAddress, target)
        patternHistoryTable.update(index, isTaken)
    }

    override fun target(pc : Int) : Int {
        return branchTargetBuffer.targetAddress(index(pc))
    }

    override fun state(pc: Int): IBitStateMachine {
        return patternHistoryTable.pattern(index(pc))
    }

    private fun index(pc : Int) : Int {
        return (pc / 4) % size
    }
}
