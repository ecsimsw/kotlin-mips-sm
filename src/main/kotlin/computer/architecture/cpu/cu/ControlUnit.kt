package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.*
import computer.architecture.cpu.register.ScoreBoardingRegisters
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val logger: Logger
) : ControlUnitInterface {
    private val scoreBoardingRegisters = ScoreBoardingRegisters(32)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()
    private val stallUnit = StallUnit()
    private val forwardingUnit = ForwardingUnit()
    private val latches = Latches()

    override fun process(): Int {
        var cycle = 0
        var validCycle = 0

        var cycleResult = CycleResult()
        val endFlag = EndFlag()

        while (true) {
            logger.printCycle(cycleResult.valid, validCycle)

            endFlag.update(cycleResult.lastInstruction)
            val pc = mux(stallUnit.isMelt, stallUnit.freezePc, cycleResult.nextPc)
            val valid = stallUnit.valid && !endFlag.isEnd

            cycleResult = cycleExecution(valid, pc)

            if (cycleResult.lastCycle) {
                return cycleResult.value
            }

            if (cycleResult.valid) {
                validCycle++
            }

            latches.flushAll()
            stallUnit.next()
            cycle++
        }
    }

    private fun cycleExecution(valid: Boolean, pc: Int): CycleResult {
        val prevIfId = latches.ifId()
        val prevIdEx = latches.idEx()
        val prevExMa = latches.exMa()
        val prevMaWb = latches.maWb()

        val fwResult1 = forwardingUnit.execute(
            prevIdEx.readData1,
            prevExMa.regWrite,
            prevExMa.aluValue,
            prevMaWb.regWrite,
            prevMaWb.regWrite
        )

        val fwResult2 = forwardingUnit.execute(
            prevIdEx.readData2,
            prevExMa.regWrite,
            prevExMa.aluValue,
            prevMaWb.regWrite,
            prevMaWb.regWrite
        )

        val nextIfId = fetch(valid, pc)
        val nextIdEx = decode(prevIfId)
        val nextExMa = execute(prevIdEx)
        val nextMaWb = memoryAccess(prevExMa)
        val wbResult = writeBack(prevMaWb)

        if (nextExMa.jump) {
            nextIfId.valid = false
            nextIdEx.valid = false
            if (nextExMa.nextPc == -1) {
                nextExMa.controlSignal.isEnd = true
            }
        }

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)

        logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        val nextPc = mux(nextExMa.jump, nextExMa.nextPc, pc + 4)
        return CycleResult(
            nextPc = nextPc,
            value = scoreBoardingRegisters[2],
            valid = wbResult.valid,
            lastInstruction = nextExMa.controlSignal.isEnd,
            lastCycle = wbResult.controlSignal.isEnd
        )
    }

    private fun fetch(valid: Boolean, pc: Int): FetchResult {
        if (!valid) {
            return FetchResult(valid, 0, 0)
        }
        val instruction = memory.read(pc)
        return FetchResult(
            valid = valid && (instruction != 0),
            pc = pc,
            instruction = instruction
        )
    }

    private fun decode(ifResult: FetchResult): DecodeResult {
        if (!ifResult.valid) {
            return DecodeResult(ifResult.valid, 0, false)
        }
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(ifResult.valid, instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)
        scoreBoardingRegisters.book(controlSignal.regWrite, writeRegister, ifResult.pc)

        return DecodeResult(
            valid = ifResult.valid,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readData1 = scoreBoardingRegisters[instruction.rs],
            readData2 = scoreBoardingRegisters[instruction.rt],
            regWrite = writeRegister,
            controlSignal = controlSignal
        )
    }

    private fun execute(idResult: DecodeResult): ExecutionResult {
        if (!idResult.valid) {
            return ExecutionResult(controlSignal = idResult.controlSignal)
        }

        val controlSignal = idResult.controlSignal
        var src1 = mux(controlSignal.shift, idResult.readData2, idResult.readData1)
        src1 = mux(controlSignal.upperImm, idResult.immediate, src1)

        var src2 = mux(controlSignal.aluSrc, idResult.immediate, idResult.readData2)
        src2 = mux(controlSignal.shift, idResult.shiftAmt, src2)
        src2 = mux(controlSignal.upperImm, 16, src2)

        val aluResult = alu.operate(
            aluOp = controlSignal.aluOp,
            src1 = src1,
            src2 = src2
        )

        val aluValue = mux(controlSignal.jal, idResult.pc + 8, aluResult.value)

        val branchCondition = and(aluResult.isTrue, controlSignal.branch)
        var nextPc = mux(branchCondition, idResult.immediate, idResult.pc)
        nextPc = mux(controlSignal.jump, idResult.address, nextPc)
        nextPc = mux(controlSignal.jr, idResult.readData1, nextPc)

        return ExecutionResult(
            valid = idResult.valid,
            pc = idResult.pc, // TODO :: only for logging
            aluValue = aluValue,
            memWriteValue = idResult.readData2,
            regWrite = idResult.regWrite,
            nextPc = nextPc,
            jump = branchCondition || controlSignal.jump || controlSignal.jr,
            controlSignal = controlSignal
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        if (!exResult.valid) {
            return MemoryAccessResult(controlSignal = exResult.controlSignal)
        }

        val controlSignal = exResult.controlSignal
        val memReadValue = memory.read(
            memRead = controlSignal.memRead,
            address = exResult.aluValue,
        )

        memory.write(
            memWrite = controlSignal.memWrite,
            address = exResult.aluValue,
            value = exResult.memWriteValue
        )

        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)

        return MemoryAccessResult(
            valid = exResult.valid,
            pc = exResult.pc, // TODO :: only for logging
            regWriteValue = regWriteValue,
            regWrite = exResult.regWrite,
            controlSignal = controlSignal
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (!maResult.valid) {
            return WriteBackResult(controlSignal = maResult.controlSignal)
        }

        if(maResult.controlSignal.regWrite) {
            scoreBoardingRegisters.write(
                writeRegister = maResult.regWrite,
                writeData = maResult.regWriteValue,
                tag = maResult.pc
            )
        }

        return WriteBackResult(
            valid = maResult.valid,
            pc = maResult.pc, // TODO :: only for logging
            regWrite = maResult.regWrite,
            regWriteValue = maResult.regWriteValue,
            controlSignal = maResult.controlSignal
        )
    }
}
