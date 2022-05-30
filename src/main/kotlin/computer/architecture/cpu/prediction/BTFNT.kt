package computer.architecture.cpu.prediction

class BTFNT : BranchPredictionStrategy() {

    override fun taken(nowPc: Int, branchPc : Int): Boolean {
        if(branchPc > nowPc) {
            return false
        }
        return true
    }
}
