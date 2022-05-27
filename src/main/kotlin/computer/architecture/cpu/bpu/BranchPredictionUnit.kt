package computer.architecture.cpu.bpu

interface BranchPredictionUnit {

    fun predict(pc :Int) : Boolean

    fun isCorrect(predictedPc : Int, calculatedPc : Int) : Boolean
}
