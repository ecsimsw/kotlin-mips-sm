package computer.architecture.cpu.prediction

class BTFNTStrategy : IBranchPredictionStrategy {

    override fun predictAsTaken(nowPc: Int, branchPc : Int): Boolean {
        if(branchPc > nowPc) {
            return false
        }
        return true
    }
}
