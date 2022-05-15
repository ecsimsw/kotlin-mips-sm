package computer.architecture.cpu

import computer.architecture.component.And.Companion.and
import computer.architecture.component.Latches
import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val logger: Logger = Logger.init(),
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val alu = ALUnit()
    private val stallingUnit = StallingUnit()
    private val dataDependencyUnit = DataDependencyUnit(registers)

    private val latches = Latches()

    fun process(): Int {
        var cycle = 0

        while (registers.pc != -1) {
            cycle++
            println("pc : " + registers.pc)
            println("stalling count : " + stallingUnit.stallingCount)

            val ifResult = fetch()
            latches.ifid(ifResult)

            val idResult = decode(latches.ifid())
            latches.idex(idResult)

            val exResult = execute(latches.idex())
            latches.exma(exResult)

            val maResult = memoryAccess(latches.exma())
            latches.mawb(maResult)

            val wbResult = writeBack(latches.mawb())
//            registers.pc = wbResult.nextPc

            logger.cycleCount(cycle)
            logger.fetchLog(ifResult)
            logger.decodeLog(idResult)
            logger.executeLog(exResult)
            logger.memoryAccessLog(maResult)
            logger.writeBackLog(wbResult)

            latches.flushAll()
            stallingUnit.next()
//            logger.saveAndFlush(cycle, ifResult, idResult, exResult, maResult, wbResult)
        }
        return registers[2]
    }

    private fun fetch(): FetchResult {
        val pc = mux(stallingUnit.isNextPc, stallingUnit.freezePc, registers.pc)
        registers.pc = pc + 4
        return FetchResult(stallingUnit.valid, pc, memory.read(pc))
    }

    private fun decode(ifResult: FetchResult): DecodeResult {
        if (!ifResult.valid) {
            return DecodeResult()
        }

        val instruction = decodeUnit.parse(registers.pc, ifResult.instruction)
        val dataValid = dataDependencyUnit.isValid(instruction.rs, instruction.rt)
        if (!dataValid) {
            stallingUnit.sleep(2, ifResult.pc)
        }

        val valid = and(ifResult.valid, dataValid)
        val controlSignal = decodeUnit.controlSignal(valid, instruction.opcode)

        var writeRegister = mux(controlSignal.regDest, instruction.rd, instruction.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)
        registers.book(controlSignal.regWrite, writeRegister)

        return DecodeResult(
            valid = valid,
            pc = ifResult.pc,
            shiftAmt = instruction.shiftAmt,
            immediate = instruction.immediate,
            address = instruction.address,
            readData1 = registers[instruction.rs],
            readData2 = registers[instruction.rt],
            writeRegister = writeRegister,
            controlSignal = controlSignal
        )
    }

    private fun execute(idResult: DecodeResult): ExecutionResult {
        if (!idResult.valid) {
            return ExecutionResult()
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

        var nextPc = registers.pc

        val branchCondition = and(aluResult.isTrue, controlSignal.branch)
        nextPc = mux(branchCondition, idResult.immediate, nextPc)
        nextPc = mux(controlSignal.jump, idResult.address, nextPc)
        nextPc = mux(controlSignal.jr, idResult.readData1, nextPc)

        return ExecutionResult(
            valid = idResult.valid,
            pc = idResult.pc,
            aluValue = aluResult.value,
            memWriteValue = idResult.readData2,
            writeRegister = idResult.writeRegister,
            nextPc = nextPc,
            controlSignal = controlSignal
        )
    }

    private fun memoryAccess(exResult: ExecutionResult): MemoryAccessResult {
        if (!exResult.valid) {
            return MemoryAccessResult()
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

        return MemoryAccessResult(
            valid = exResult.valid,
            pc = exResult.pc,
            memReadValue = memReadValue,
            memWriteValue = exResult.memWriteValue,
            aluValue = exResult.aluValue,
            writeRegister = exResult.writeRegister,
            nextPc = exResult.nextPc,
            controlSignal = controlSignal
        )
    }

    private fun writeBack(maResult: MemoryAccessResult): WriteBackResult {
        if (!maResult.valid) {
            return WriteBackResult()
        }

        val controlSignal = maResult.controlSignal
        var regWriteValue = mux(controlSignal.memToReg, maResult.memReadValue, maResult.aluValue)
        regWriteValue = mux(controlSignal.jal, registers.pc + 4, regWriteValue)

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = maResult.writeRegister,
            writeData = regWriteValue
        )

        return WriteBackResult(
            valid = maResult.valid,
            pc = maResult.pc,
            writeRegister = maResult.writeRegister,
            regWriteValue = regWriteValue,
            nextPc = maResult.nextPc,
            controlSignal = controlSignal
        )
    }
}
