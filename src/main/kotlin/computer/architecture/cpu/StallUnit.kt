package computer.architecture.cpu

import computer.architecture.component.Mux.Companion.mux

class StallUnit(
    var stallingCount: Int = 0
) {
    var freezePc: Int = 0
    var isMelt: Boolean = false
    var valid: Boolean = true

    fun next(nextPc : Int) : Int {
        next()
        return mux(isMelt, freezePc, nextPc)
    }

    private fun next() {
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

    fun sleep(stallingCount: Int, pc: Int) {
        if (this.valid) {
            this.freezePc = pc
            this.valid = false
            this.stallingCount = stallingCount
        }
    }
}
