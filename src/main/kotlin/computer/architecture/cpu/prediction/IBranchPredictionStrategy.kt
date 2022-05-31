package computer.architecture.cpu.prediction

interface IBranchPredictionStrategy {

    fun predictAsTaken(nowPc: Int, branchPc: Int): Boolean
}
