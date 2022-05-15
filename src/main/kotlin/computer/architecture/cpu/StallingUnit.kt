package computer.architecture.cpu

class StallingUnit(
    private var stallingCount: Int = 0
) {
    var freezePc: Int = 0
    var isNextPc: Boolean = false
    var valid: Boolean = true

    fun next() {
        when (stallingCount) {
            0 -> isNextPc = false
            1 -> {
                stallingCount = 0
                isNextPc = true
                valid = true
            }
            else -> stallingCount--
        }
    }

    fun takeDependency(dataDependencyResult: DataDependencyResult) {
        if (!dataDependencyResult.valid) {
            sleep(stallingCount, dataDependencyResult.freezePc)
        }
    }

    private fun sleep(stallingCount: Int, pc: Int) {
        this.freezePc = pc
        this.valid = false
        this.stallingCount = stallingCount
    }
}
