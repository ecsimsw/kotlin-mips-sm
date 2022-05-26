package computer.architecture.cpu.pu

interface BranchPredictionUnit {

    fun predict(pc :Int) : Boolean
}
