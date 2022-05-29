package computer.architecture.cpu.cu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Or.Companion.or
import computer.architecture.cpu.*
import computer.architecture.cpu.register.ScoreBoardingRegisters
import computer.architecture.utils.Logger

class ControlUnit_Stall_Stall(
    private val memory: Memory,
    private val logger: Logger,
    private val pcUnit: IProgramCounterUnit = StallingPcUnit()
) : IControlUnit {
    private val registers = ScoreBoardingRegisters(32)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()
    private val stallUnit = StallUnit()
    private val dataDependencyUnit = DataDependencyUnit(registers)
    private val latches = Latches()

    override fun process(): Int {
        var cycle = 0
        var cycleResult = CycleResult()
        var isEnd = false

        logger.init()
        while (true) {
            logger.printCycle(cycleResult.valid, cycle)

            isEnd = or(isEnd, cycleResult.isEnd)
            val pc = stallUnit.next(cycleResult.nextPc)
            val valid = stallUnit.valid && !isEnd

            cycleResult = cycleExecution(valid, pc)

            if (cycleResult.lastCycle) {
                return cycleResult.value
            }

            latches.flushAll()
            cycle++
        }
    }

    private fun cycleExecution(valid: Boolean, pc: Int): CycleResult {
        val nextIfId = fetch(valid, pc)
        val nextIdEx = decode(latches.ifId())
        if (nextIdEx.valid) {
            val dependencyResult = dataDependencyUnit.execute(nextIdEx, nextIfId)
            if (dependencyResult.isHazard) {
                stallUnit.sleep(2, dependencyResult.freezePc)
            }
        }

        val nextExMa = execute(latches.idEx())
        val nextMaWb = memoryAccess(latches.exMa())
        val wbResult = writeBack(latches.maWb())
        val nextPcInfo = pcUnit.findNext(pc, nextIfId, nextIdEx, nextExMa)

        latches.store(nextIfId)
        latches.store(nextIdEx)
        latches.store(nextExMa)
        latches.store(nextMaWb)
        latches.flushAll()
        logger.log(nextIfId, nextIdEx, nextExMa, nextMaWb, wbResult)

        return CycleResult(
            nextPc = nextPcInfo.nextPc,
            value = registers[2],
            valid = wbResult.valid,
            isEnd = nextPcInfo.isEnd,
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
        val instruction = decodeUnit.parse(ifResult.pc + 4, ifResult.instruction)
        val controlSignal = decodeUnit.controlSignal(ifResult.valid, instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        var nextPc = mux(controlSignal.jump, instruction.address, ifResult.pc)
        nextPc = mux(controlSignal.jr, registers[instruction.rs], nextPc)

        return DecodeResult(
            valid = ifResult.valid,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readReg1 = instruction.rs,
            readReg2 = instruction.rt,
            readData1 = registers[instruction.rs],
            readData2 = registers[instruction.rt],
            writeReg = writeRegister,
            jump = controlSignal.jump || controlSignal.jr,
            nextPc = nextPc,
            controlSignal = controlSignal
        )
    }

    private fun execute(idResult: DecodeResult): ExecutionResult {
        val controlSignal = idResult.controlSignal
        val aluValue = alu.execute(idResult)

        val branchCondition = and(aluValue == 1, controlSignal.branch)
        return ExecutionResult(
            valid = idResult.valid,
            pc = idResult.pc, // TODO :: only for logging
            readData2 = idResult.readData2,
            writeReg = idResult.writeReg,
            aluValue = aluValue,
            nextPc = idResult.immediate,
            branch = branchCondition,
            controlSignal = controlSignal
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        val controlSignal = exResult.controlSignal
        val memReadValue = memory.read(
            memRead = controlSignal.memRead,
            address = exResult.aluValue,
        )

        val regWriteValue = mux(controlSignal.memToReg, memReadValue, exResult.aluValue)

        memory.write(
            memWrite = controlSignal.memWrite,
            address = exResult.aluValue,
            value = exResult.readData2
        )

        return MemoryAccessResult(
            valid = exResult.valid,
            pc = exResult.pc, // TODO :: only for logging
            regWriteValue = regWriteValue,
            address = exResult.aluValue, // TODO :: only for logging
            memReadValue = memReadValue, // TODO :: only for logging
            memWriteValue =  exResult.readData2, // TODO :: only for logging
            writeReg = exResult.writeReg,
            controlSignal = controlSignal
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (maResult.controlSignal.regWrite) {
            registers.write(
                writeRegister = maResult.writeReg,
                writeData = maResult.regWriteValue,
                tag = maResult.pc
            )
        }

        return WriteBackResult(
            valid = maResult.valid,
            pc = maResult.pc, // TODO :: only for logging
            writeReg = maResult.writeReg,
            regWriteValue = maResult.regWriteValue,
            controlSignal = maResult.controlSignal,
        )
    }
}
