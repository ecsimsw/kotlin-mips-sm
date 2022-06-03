package computer.architecture.cpu.cu

import computer.architecture.component.Memory
import computer.architecture.cpu.CycleResult
import computer.architecture.cpu.ForwardingUnit
import computer.architecture.cpu.pc.IProgramCounterUnit
import computer.architecture.cpu.pc.NonePredictionPcUnit
import computer.architecture.utils.Logger

class ForwardingPipeLineControlUnit(
    memory: Memory,
    private val pcUnit: IProgramCounterUnit = NonePredictionPcUnit()
) : PipeLineControlUnit(memory) {
    private val forwardingUnit = ForwardingUnit()

    override fun cycleExecution(valid: Boolean, pc: Int): CycleResult {
        val prevIfId = latches.ifId()
        val prevIdEx = latches.idEx()
        val prevExMa = latches.exMa()
        val prevMaWb = latches.maWb()

        val wbResult = writeBack(prevMaWb)
        val nextMaWb = memoryAccess(prevExMa)
        forwardingUnit.forward(prevIdEx, prevExMa, prevMaWb)
        val nextExMa = execute(prevIdEx)
        val nextIdEx = decode(prevIfId)
        val nextIfId = fetch(valid, pc)
        val nextPc = pcUnit.findNext(pc, nextIfId, nextIdEx, nextExMa)

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)
        Logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        return CycleResult(
            nextPc = nextPc,
            value = registers[2],
            valid = wbResult.valid,
            isEnd = nextPc == -1,
            lastCycle = wbResult.controlSignal.isEnd
        )
    }
}
