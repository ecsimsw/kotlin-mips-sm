package computer.architecture.cpu.prediction

class AlwaysNotTakenStrategy : IBranchPredictionStrategy {

    override fun predictAsTaken(nowPc: Int, branchPc: Int): Boolean {
        return false
    }
}
