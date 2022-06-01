package computer.architecture.cpu.bht

class BranchHistoryTable(
    size: Int
) {
    private val historyTable = mutableMapOf<Int, BranchHistory>()

    init {
        for (i in 0..size) {
            historyTable[i] = BranchHistory(-1, -1)
        }
    }

    fun update(index: Int, branchAddress: Int, targetAddress : Int) {
        historyTable[index]!!.branchAddress = branchAddress
        historyTable[index]!!.targetAddress = targetAddress
    }

    fun targetAddress(index: Int): Int {
        return historyTable[index]!!.targetAddress
    }

    fun isHit(index: Int, pc : Int): Boolean {
        return historyTable[index]!!.branchAddress == pc
    }
}
