package computer.architecture.cpu

class StallUnit(
    var stallingCount: Int = 0
) {
    var freezePc: Int = 0
    var isMelt: Boolean = false
    var valid: Boolean = true

    fun next() {
        when (stallingCount) {
            0 -> isMelt = false
            1 -> {
                stallingCount = 0
                isMelt = true
                valid = true
            }
            else -> stallingCount--
        }
    }

    fun sleep(stallingCount: Int, pc : Int) {
        if (this.valid) {
            this.freezePc = pc
            this.valid = false
            this.stallingCount = stallingCount
        }
    }
}
