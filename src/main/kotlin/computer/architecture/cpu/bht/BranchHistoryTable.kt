package computer.architecture.cpu.prediction

import computer.architecture.cpu.bht.BranchHistory

open class BranchHistoryTable {

    val histories = listOf(
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0000
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0001
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0010
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0011
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0100
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0101
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0110
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 0111
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1000
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1001
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1010
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1011
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1100
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1101
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1110
        BranchHistory(-1,-1, SaturationTwoBitStateMachine()), // 1111
    )

    fun isHit(pc : Int) : Boolean {
        val index = (pc / 4) % 16
        return histories[index].branchAddress == pc
    }

    fun update(branchAddress:Int, target: Int, isTaken: Boolean) {
        val index = (branchAddress / 4) % 16
        histories[index].branchAddress = branchAddress
        histories[index].targetAddress = target
        histories[index].state.change(isTaken)
    }

    fun target(pc : Int) : Int {
        val index = (pc / 4) % 16
        return histories[index].targetAddress
    }

    fun state(pc: Int): BitStateMachine {
        val index = (pc / 4) % 16
        return histories[index].state
    }
}
