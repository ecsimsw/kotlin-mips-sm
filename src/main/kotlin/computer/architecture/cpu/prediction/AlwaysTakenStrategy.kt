package computer.architecture.cpu.prediction

class AlwaysTakenStrategy : IBranchPredictionStrategy {

    override fun predictAsTaken(nowPc: Int, branchPc: Int): Boolean {
        return true
    }
}
