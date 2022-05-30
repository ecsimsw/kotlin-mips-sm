package computer.architecture.cpu.prediction

class AlwaysNotTakenStrategy : BranchPredictionStrategy() {
    override fun taken(nowPc: Int, branchPc: Int): Boolean {
        return false
    }
}
