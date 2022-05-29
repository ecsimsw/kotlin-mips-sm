package computer.architecture.cpu.prediction

class AlwaysNotTakenStrategy : BranchPredictionStrategy() {

    override fun taken(pc: Int): Boolean {
        return false
    }
}
