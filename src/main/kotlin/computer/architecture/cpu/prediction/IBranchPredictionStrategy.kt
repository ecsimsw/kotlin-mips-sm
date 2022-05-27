package computer.architecture.cpu.prediction

interface IBranchPredictionStrategy {

    fun taken(pc :Int) : Boolean

    fun isCorrect(predictedPc : Int, calculatedPc : Int) : Boolean
}
