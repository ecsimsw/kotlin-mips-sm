package computer.architecture.cpu.bht

data class BranchHistory(
    var branchAddress: Int,
    var targetAddress: Int,
)
