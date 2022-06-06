package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.ALUnit
import computer.architecture.cpu.DecodeUnit
import computer.architecture.cpu.SchedulingUnit
import computer.architecture.cpu.cache.ICache
import computer.architecture.cpu.cache.WriteBackDirectMappedCache
import computer.architecture.cpu.dto.*
import computer.architecture.cpu.register.Registers
import computer.architecture.utils.Logger

class MultiProcessingPipelineControlUnit(
    private val caches: List<ICache>
) : IControlUnit {
    private val registers: List<Registers> = List(caches.size) { Registers(32) }
    private val schedulingUnit = SchedulingUnit(caches.size)
    private val latches = Latches()
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()

    override fun process(): List<Int> {
        var cycle = 0

        Logger.init()
        while (!schedulingUnit.isAllEnd()) {
            Logger.printCycle(cycle)

            val programInfo = schedulingUnit.next()
            val isProcessEnd = programInfo.processEnd
            val cycleResult = cycleExecution(!isProcessEnd, programInfo.pn, programInfo.nextPc)
            schedulingUnit.update(cycleResult)

            latches.flushAll()
            cycle++
        }
        return registers.map { it[2] }.toList()
    }

    private fun cycleExecution(valid: Boolean, pn: Int, pc: Int): CycleResult {
        val nextIfId = fetch(valid, pn, pc)
        val nextIdEx = decode(latches.ifId())
        val nextExMa = execute(latches.idEx())
        val nextMaWb = memoryAccess(latches.exMa())
        val wbResult = writeBack(latches.maWb())

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)
        latches.flushAll()
        Logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        if (!wbResult.valid) {
            return CycleResult(
                nextPc = wbResult.nextPc,
                value = 0,
                valid = true,
                isEnd = wbResult.nextPc == -1,
                lastCycle = wbResult.controlSignal.isEnd,
                pn = wbResult.pn
            )
        }

        return CycleResult(
            nextPc = wbResult.nextPc,
            value = registers[wbResult.pn][2],
            valid = true,
            isEnd = wbResult.nextPc == -1,
            lastCycle = wbResult.controlSignal.isEnd,
            pn = wbResult.pn
        )
    }

    fun fetch(valid: Boolean, pn: Int, pc: Int): FetchResult {
        if (!valid) {
            return FetchResult(valid, 0, 0, pn)
        }
        return FetchResult(
            valid = true,
            pc = pc,
            instruction = caches[pn].read(pc),
            pn = pn
        )
    }

    private fun decode(ifResult: FetchResult): DecodeResult {
        if (!ifResult.valid) {
            return DecodeResult()
        }

        val processRegister = registers[ifResult.pn]
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(ifResult.valid, instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        var nextPc = mux(controlSignal.jump, instruction.address, ifResult.pc)
        nextPc = mux(controlSignal.jr, processRegister[instruction.rs], nextPc)

        return DecodeResult(
            valid = true,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readReg1 = instruction.rs,
            readReg2 = instruction.rt,
            readData1 = processRegister[instruction.rs],
            readData2 = processRegister[instruction.rt],
            writeReg = writeRegister,
            jump = controlSignal.jump || controlSignal.jr,
            nextPc = nextPc,
            controlSignal = controlSignal,
            pn = ifResult.pn
        )
    }

    private fun execute(idResult: DecodeResult): ExecutionResult {
        if (!idResult.valid) {
            return ExecutionResult()
        }

        val controlSignal = idResult.controlSignal
        val aluValue = alu.execute(idResult)

        val branchCondition = and(aluValue == 1, controlSignal.branch)
        var nextPc = mux(branchCondition, idResult.immediate, idResult.pc + 4)
        nextPc = mux(idResult.jump, idResult.nextPc, nextPc)

        return ExecutionResult(
            valid = true,
            pc = idResult.pc,
            readData2 = idResult.readData2,
            writeReg = idResult.writeReg,
            aluValue = aluValue,
            nextPc = nextPc,
            branch = branchCondition,
            controlSignal = controlSignal,
            pn = idResult.pn
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        if (!exResult.valid) {
            return MemoryAccessResult()
        }

        val controlSignal = exResult.controlSignal
        val memReadValue = if (controlSignal.memRead) caches[exResult.pn].read(exResult.aluValue) else 0
        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)

        if (controlSignal.memWrite) {
            caches[exResult.pn].write(exResult.aluValue, exResult.readData2)
        }

        return MemoryAccessResult(
            valid = true,
            pc = exResult.pc,
            regWriteValue = regWriteValue,
            address = exResult.aluValue,
            memReadValue = memReadValue,
            memWriteValue = exResult.readData2,
            writeReg = exResult.writeReg,
            nextPc = exResult.nextPc,
            controlSignal = controlSignal,
            pn = exResult.pn
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (!maResult.valid) {
            return WriteBackResult()
        }

        val processRegister = registers[maResult.pn]

        if (maResult.controlSignal.regWrite) {
            processRegister.write(
                register = maResult.writeReg,
                data = maResult.regWriteValue,
            )
        }

        return WriteBackResult(
            valid = true,
            pc = maResult.pc,
            regWriteValue = maResult.regWriteValue,
            writeReg = maResult.writeReg,
            controlSignal = maResult.controlSignal,
            nextPc = maResult.nextPc,
            pn = maResult.pn
        )
    }
}
