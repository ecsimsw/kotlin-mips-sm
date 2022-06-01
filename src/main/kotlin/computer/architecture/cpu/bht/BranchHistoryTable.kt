package computer.architecture.cpu.prediction

import computer.architecture.cpu.bht.BranchHistory

open class BranchHistoryTable(
    private val size: Int = 16,
) {

    private val histories = Array(size) {
        BranchHistory(-1,-1, SaturationTwoBitStateMachine())
    }

    fun isHit(pc : Int) : Boolean {
        return histories[index(pc)].branchAddress == pc
    }

    fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = index(branchAddress)
        histories[index].branchAddress = branchAddress
        histories[index].targetAddress = target
        histories[index].state.change(isTaken)
    }

    fun target(pc : Int) : Int {
        return histories[index(pc)].targetAddress
    }

    fun state(pc: Int): BitStateMachine {
        return histories[index(pc)].state
    }

    private fun index(pc : Int) : Int {
        return (pc / 4) % size
    }
}
