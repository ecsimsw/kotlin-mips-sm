package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.cpu.*
import computer.architecture.cpu.register.Registers
import computer.architecture.utils.Logger

class ControlUnit_Forwarding_Stall(
    private val memory: Memory,
    private val logger: Logger
) : ControlUnitInterface {
    private val registers = Registers(32)
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

        val wbResult = writeBack(prevMaWb)
        val nextMaWb = memoryAccess(prevExMa)

        forwardingUnit.execute(prevIdEx, prevExMa, prevMaWb)

        val nextExMa = execute(prevIdEx)
        val nextIdEx = decode(prevIfId)
        val nextIfId = fetch(valid, pc)

        if (nextExMa.jump) {
            nextIfId.valid = false
            nextIdEx.valid = false
            if (nextExMa.nextPc == -1) {
                nextExMa.controlSignal.isEnd = true
            }
        }

        val nextPc = mux(nextExMa.jump, nextExMa.nextPc, pc + 4)

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)

        logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        return CycleResult(
            nextPc = nextPc,
            value = registers[2],
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
            return DecodeResult(ifResult.valid, 0)
        }
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(ifResult.valid, instruction.opcode)

        val readData1 = registers[instruction.rs]
        val readData2 = registers[instruction.rt]

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            valid = ifResult.valid,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readReg1 = instruction.rs,
            readReg2 = instruction.rt,
            readData1 = readData1,
            readData2 = readData2,
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

        if (maResult.controlSignal.regWrite) {
            registers.write(
                register = maResult.regWrite,
                data = maResult.regWriteValue,
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
