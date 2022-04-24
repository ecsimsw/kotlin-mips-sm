package computer.architecture.cpu

import computer.architecture.component.And.Companion.and
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
    private var controlSignal = ControlSignal()

    fun process(): Int {
        var cycleCount = 0

        while (registers.pc != -1) {
            cycleCount++
            val ifResult = fetch(registers.pc)
            val idResult = decode(ifResult)
            val exResult = execute(idResult)
            val maResult = memoryAccess(exResult)
            val wbResult = writeBack(maResult)
            registers.pc = wbResult.nextPc

            logger.cycleCount(cycleCount)
            logger.fetchLog(cycleCount, ifResult)
            logger.decodeLog(controlSignal, idResult)
            logger.executeLog(controlSignal, exResult)
            logger.memoryAccessLog(controlSignal, exResult.aluValue, maResult.readData, exResult.memWriteData)
            logger.writeBackLog(controlSignal, wbResult)
        }
        return registers[2]
    }

    private fun fetch(pc: Int): FetchResult {
        val fetchResult = FetchResult(registers.pc, memory.read(pc))
        registers.pc += 4
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val parsedInst = decodeUnit.parse(registers.pc, fetchResult.instruction)
        controlSignal = decodeUnit.controlSignal(parsedInst.opcode)

        var writeRegister = mux(controlSignal.regDest, parsedInst.rd, parsedInst.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            shiftAmt = parsedInst.shiftAmt,
            immediate = parsedInst.immediate,
            address = parsedInst.address,
            readData1 = registers[parsedInst.rs],
            readData2 = registers[parsedInst.rt],
            writeRegister = writeRegister
        )
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        var src1 = mux(controlSignal.shift, decodeResult.readData2, decodeResult.readData1)
        src1 = mux(controlSignal.upperImm, decodeResult.immediate, src1)

        var src2 = mux(controlSignal.aluSrc, decodeResult.immediate, decodeResult.readData2)
        src2 = mux(controlSignal.shift, decodeResult.shiftAmt, src2)
        src2 = mux(controlSignal.upperImm, 16, src2)

        val aluResult = alu.operate(
            aluOp = controlSignal.aluOp,
            src1 = src1,
            src2 = src2
        )

        var nextPc = registers.pc

        val branchCondition = and(aluResult.isTrue, controlSignal.branch)
        nextPc = mux(branchCondition, decodeResult.immediate, nextPc)
        nextPc = mux(controlSignal.jump, decodeResult.address, nextPc)
        nextPc = mux(controlSignal.jr, decodeResult.readData1, nextPc)

        return ExecutionResult(
            aluValue = aluResult.value,
            memWriteData = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister,
            nextPc = nextPc
        )
    }

    private fun memoryAccess(executionResult: ExecutionResult): MemoryAccessResult {
        val readData = memory.read(
            memRead = controlSignal.memRead,
            address = executionResult.aluValue,
        )

        memory.write(
            memWrite = controlSignal.memWrite,
            address = executionResult.aluValue,
            value = executionResult.memWriteData
        )

        return MemoryAccessResult(
            readData = readData,
            aluValue = executionResult.aluValue,
            writeRegister = executionResult.writeRegister,
            nextPc = executionResult.nextPc
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        var writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluValue)
        writeData = mux(controlSignal.jal, registers.pc + 4, writeData)

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )

        return WriteBackResult(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData,
            nextPc = memoryAccessResult.nextPc
        )
    }
}
