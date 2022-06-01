package computer.architecture.cpu

import computer.architecture.component.Or.Companion.or

class SchedulingUnit(
    private val size: Int
) {
    private var lastProcessIndex = -1

    private val programNumbers = List(size) {
        ProgramInfo(
            pn = it,
            processEnd = false,
            ignoreFetch = false,
            isLastCycle = false,
            nextPc = 0
        )
    }

    fun pop(): ProgramInfo {
        lastProcessIndex = (lastProcessIndex + 1) % size
        return programNumbers[lastProcessIndex]
    }

    fun isAllEnd(): Boolean {
        return programNumbers.none { !it.processEnd }
    }

    fun end(pn: Int) {
        programNumbers[pn].processEnd = true
    }
}

data class ProgramInfo(
    var pn: Int = 0,
    var processEnd: Boolean = false,
    var ignoreFetch: Boolean = false,
    var isLastCycle: Boolean = false,
    var nextPc: Int = 0
) {
    fun update(cycleResult: CycleResult) {
        this.ignoreFetch = or(this.ignoreFetch, cycleResult.isEnd)
        this.nextPc = cycleResult.nextPc
        this.isLastCycle = cycleResult.lastCycle
    }
}
