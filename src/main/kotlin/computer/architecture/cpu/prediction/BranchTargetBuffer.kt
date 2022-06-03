package computer.architecture.cpu.prediction

class BranchTargetBuffer(
    size: Int
) {
    private val tags = Array(size) { 0 }
    private val targetAddresses = Array(size) { 0 }

    fun update(index: Int, branchAddress: Int, targetAddress: Int) {
        tags[index] = branchAddress
        targetAddresses[index] = targetAddress
    }

    fun targetAddress(index: Int): Int {
        return targetAddresses[index]
    }

    fun isHit(index: Int, pc: Int): Boolean {
        return tags[index] == pc
    }
}
