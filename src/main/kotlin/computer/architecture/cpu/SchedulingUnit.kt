package computer.architecture.cpu

import computer.architecture.cpu.dto.CycleResult

class SchedulingUnit(
    private val size: Int
) {
    private var lastProcessIndex = -1

    private val programNumbers = List(size) {
        ProgramInfo(
            pn = it,
            processEnd = false,
            nextPc = 0,
            fetchAlive = true
        )
    }

    fun next(): ProgramInfo {
        for (i in 0..size) {
            lastProcessIndex = (lastProcessIndex + 1) % size
            if (programNumbers[lastProcessIndex].fetchAlive) {
                return programNumbers[lastProcessIndex]
            }
        }
        throw IllegalArgumentException("No process to fetch")
    }

    fun isAllEnd(): Boolean {
        return programNumbers.none { !it.processEnd }
    }

    fun update(cycleResult: CycleResult) {
        if (!cycleResult.valid || cycleResult.pn == -1) {
            return
        }
        if (cycleResult.nextPc == -1 && programNumbers.count { !it.fetchAlive } > 3) {
            programNumbers[cycleResult.pn].update(cycleResult, false)
            return
        }
        programNumbers[cycleResult.pn].update(cycleResult, true)
    }
}

data class ProgramInfo(
    var pn: Int = 0,
    var processEnd: Boolean = false,
    var nextPc: Int = 0,
    var fetchAlive: Boolean = true
) {
    fun update(cycleResult: CycleResult, fetchAlive: Boolean) {
        this.nextPc = cycleResult.nextPc
        this.processEnd = cycleResult.nextPc == -1
        this.fetchAlive = fetchAlive
    }
}
