package computer.architecture.cpu

class StallingUnit(
    private var stallingCount: Int = 0
){
    var freezePc: Int =0
    var isNextPc: Boolean = false
    var valid : Boolean = true

    fun next() {
        if(stallingCount > 0) {
            stallingCount--
            if (stallingCount == 0) {
                isNextPc = true
            }
            return
        }
        isNextPc = false
    }

    fun sleep(stallingCount: Int, nextPc : Int) {
        this.stallingCount = stallingCount
        this.freezePc = nextPc
    }
}
