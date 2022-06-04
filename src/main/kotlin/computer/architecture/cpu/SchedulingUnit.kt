package computer.architecture.cpu

class SchedulingUnit(
    private val size: Int
) {
    private var lastProcessIndex = -1

    private val programNumbers = List(size) {
        ProgramInfo(
            pn = it,
            processEnd = false,
            nextPc = 0
        )
    }

    fun next(): ProgramInfo {
        lastProcessIndex = (lastProcessIndex + 1) % size
        return programNumbers[lastProcessIndex]
    }

    fun isAllEnd(): Boolean {
        return programNumbers.none { !it.processEnd }
    }

    fun update(cycleResult: CycleResult) {
        if (!cycleResult.valid || cycleResult.pn == -1) {
            return
        }
        programNumbers[cycleResult.pn].update(cycleResult)
    }
}

data class ProgramInfo(
    var pn: Int = 0,
    var processEnd: Boolean = false,
    var nextPc: Int = 0
) {
    fun update(cycleResult: CycleResult) {
        this.nextPc = cycleResult.nextPc
        this.processEnd = cycleResult.nextPc == -1
    }
}
