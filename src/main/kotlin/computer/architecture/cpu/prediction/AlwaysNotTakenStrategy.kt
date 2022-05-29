package computer.architecture.cpu.prediction

class AlwaysNotTakenStrategy : IBranchPredictionStrategy {

    private var correctCount : Int = 0
    private var incorrectCount : Int = 0

    override fun taken(pc: Int): Boolean {
        return false
    }

    override fun isCorrect(predictedPc: Int, calculatedPc: Int): Boolean {
        if(predictedPc == calculatedPc) {
            correctCount++
            return true
        }
        incorrectCount++
        return false
    }
}

