package computer.architecture.cpu.bht

import computer.architecture.cpu.prediction.BitStateMachine

data class BranchHistory(
    var branchAddress : Int,
    var targetAddress : Int,
    val state: BitStateMachine
)
