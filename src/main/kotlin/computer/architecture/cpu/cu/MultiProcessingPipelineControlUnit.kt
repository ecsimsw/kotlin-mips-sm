package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.*
import computer.architecture.cpu.register.Registers
import computer.architecture.utils.Logger

class MultiProcessingPipelineControlUnit(
    private val memories: List<Memory>,
) {
    private val registers: List<Registers> = List(memories.size) { Registers(32) }
    private val schedulingUnit = SchedulingUnit(memories.size)
    private val latches = Latches()
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()

    fun process(): List<Int> {
        var cycle = 0

        Logger.init()
        while (true) {
            Logger.printCycle(cycle)

            val programInfo = schedulingUnit.pop()

            val isNop = programInfo.ignoreFetch || programInfo.processEnd
            val cycleResult = cycleExecution(!isNop, programInfo.pn, programInfo.nextPc)

            programInfo.update(cycleResult)
            if(cycleResult.lastCycle) {
                schedulingUnit.end(cycleResult.pn)
            }

            if (schedulingUnit.isAllEnd()) {
                break
            }

            latches.flushAll()
            cycle++
        }
        return registers.map { it[2] }.toList()
    }

    fun cycleExecution(valid: Boolean, pn: Int, pc: Int): CycleResult {
        val nextIfId = fetch(valid, pn, pc)
        val nextIdEx = decode(latches.ifId())
        val nextExMa = execute(latches.idEx())
        val nextMaWb = memoryAccess(latches.exMa())
        val wbResult = writeBack(latches.maWb())
        val nextPc = findNext(pc, nextIdEx, nextExMa)

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)
        latches.flushAll()
        Logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        if(!wbResult.valid) {
            return CycleResult(
                nextPc = nextPc,
                value = 0,
                valid = true,
                isEnd = nextPc == -1,
                lastCycle = wbResult.controlSignal.isEnd,
                pn = wbResult.pn
            )
        }

        return CycleResult(
            nextPc = nextPc,
            value = registers[wbResult.pn][2],
            valid = true,
            isEnd = nextPc == -1,
            lastCycle = wbResult.controlSignal.isEnd,
            pn = wbResult.pn
        )
    }

    private fun findNext(
        pc: Int,
        nextIdEx: DecodeResult,
        nextExMa: ExecutionResult
    ): Int {
        if (nextExMa.valid && nextExMa.branch) {
            val nextPc = nextExMa.nextPc
            if (nextPc == -1) {
                nextExMa.controlSignal.isEnd = true
            }
            return nextPc
        }

        if (nextIdEx.valid && nextIdEx.jump) {
            val nextPc = nextIdEx.nextPc
            if (nextPc == -1) {
                nextIdEx.controlSignal.isEnd = true
            }
            return nextPc
        }
        return pc + 4
    }

    fun fetch(valid: Boolean, pn: Int, pc: Int): FetchResult {
        if (!valid) {
            return FetchResult(valid, 0, 0, pn)
        }
        return FetchResult(
            valid = true,
            pc = pc,
            instruction = memories[pn].read(pc),
            pn = pn
        )
    }

    private fun decode(ifResult: FetchResult): DecodeResult {
        if(!ifResult.valid) {
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
        if(!idResult.valid) {
            return ExecutionResult()
        }

        val controlSignal = idResult.controlSignal
        val aluValue = alu.execute(idResult)

        val branchCondition = and(aluValue == 1, controlSignal.branch)
        return ExecutionResult(
            valid = true,
            pc = idResult.pc, // TODO :: only for logging
            readData2 = idResult.readData2,
            writeReg = idResult.writeReg,
            aluValue = aluValue,
            nextPc = idResult.immediate,
            branch = branchCondition,
            controlSignal = controlSignal,
            pn = idResult.pn
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        if(!exResult.valid) {
            return MemoryAccessResult()
        }

        val processMemory = memories[exResult.pn]

        val controlSignal = exResult.controlSignal
        val memReadValue = processMemory.read(
            memRead = controlSignal.memRead,
            address = exResult.aluValue,
        )

        processMemory.write(
            memWrite = controlSignal.memWrite,
            address = exResult.aluValue,
            value = exResult.readData2
        )

        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)

        return MemoryAccessResult(
            valid = true,
            pc = exResult.pc, // TODO :: only for logging
            regWriteValue = regWriteValue,
            address = exResult.aluValue, // TODO :: only for logging
            memReadValue = memReadValue, // TODO :: only for logging
            memWriteValue = exResult.readData2, // TODO :: only for logging
            writeReg = exResult.writeReg,
            controlSignal = controlSignal,
            pn = exResult.pn
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if(!maResult.valid) {
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
            pc = maResult.pc, // TODO :: only for logging
            regWriteValue = maResult.regWriteValue,
            writeReg = maResult.writeReg,
            controlSignal = maResult.controlSignal,
            pn = maResult.pn
        )
    }
}
