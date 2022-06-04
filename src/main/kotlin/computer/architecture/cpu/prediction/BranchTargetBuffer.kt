package computer.architecture.cpu.prediction

class BranchTargetBuffer(
    val size: Int
) {
    private val tags = Array(size) { 0 }
    private val targetAddresses = Array(size) { 0 }

    fun update(branchAddress: Int, targetAddress: Int) {
        val index = index(branchAddress)
        tags[index] = branchAddress
        targetAddresses[index] = targetAddress
    }

    fun targetAddress(pc: Int): Int {
        val index = index(pc)
        return targetAddresses[index]
    }

    fun isHit(pc: Int): Boolean {
        return tags[index(pc)] == pc
    }

    private fun index(pc: Int): Int {
        return (pc / 4) % size
    }
}
