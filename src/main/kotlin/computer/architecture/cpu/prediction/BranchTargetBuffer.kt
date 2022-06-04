package computer.architecture.cpu.prediction

class BranchTargetBuffer(
    val size: Int
) {
    private val valid = Array(size) { false }
    private val tags = Array(size) { 0 }
    private val targetAddresses = Array(size) { 0 }

    fun update(pc: Int, targetAddress: Int) {
        val index = index(pc)
        valid[index] = true
        tags[index] = tag(pc)
        targetAddresses[index] = targetAddress
    }

    fun targetAddress(pc: Int): Int {
        val index = index(pc)
        return targetAddresses[index]
    }

    fun isHit(pc: Int): Boolean {
        val index = index(pc)
        return valid[index] && tags[index] == tag(pc)
    }

    private fun index(pc: Int): Int {
        return (pc / 4) % size
    }

    private fun tag(pc: Int): Int {
        return (pc / 4) / size
    }
}
