package computer.architecture.cpu.prediction

class AlwaysTakenStrategy : BranchPredictionStrategy() {

    override fun taken(pc: Int): Boolean {
        return true
    }
}
