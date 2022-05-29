package computer.architecture.cpu.prediction

import computer.architecture.cpu.ExecutionResult

interface IBranchPredictionStrategy {

    fun taken(pc :Int) : Boolean
    fun predict(nextExMa: ExecutionResult): Int
}
