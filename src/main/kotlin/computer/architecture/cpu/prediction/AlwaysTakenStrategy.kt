package computer.architecture.cpu.prediction

class AlwaysTakenStrategy : BranchPredictionStrategy() {

    override fun taken(nowPc: Int, branchPc: Int): Boolean {
        return true
    }
}
