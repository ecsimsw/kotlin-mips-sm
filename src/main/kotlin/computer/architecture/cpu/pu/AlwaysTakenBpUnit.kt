package computer.architecture.cpu.pu

class AlwaysTakenBpUnit : BranchPredictionUnit {

    override fun predict(pc: Int): Boolean {
        return true
    }
}
