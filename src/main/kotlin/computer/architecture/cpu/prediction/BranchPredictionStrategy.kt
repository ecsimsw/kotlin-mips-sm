package computer.architecture.cpu.prediction

import computer.architecture.cpu.ExecutionResult

abstract class BranchPredictionStrategy : IBranchPredictionStrategy {

    abstract override fun taken(nowPc: Int, branchPc: Int): Boolean

    override fun predict(nextExMa: ExecutionResult): Int {
        return if (taken(nextExMa.pc, nextExMa.nextPc)) {
            nextExMa.pc + 4
        } else {
            nextExMa.nextPc
        }
    }
}