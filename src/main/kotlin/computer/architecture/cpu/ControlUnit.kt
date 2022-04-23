package computer.architecture.cpu

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
            val fetchResult = fetch(registers.pc)
            val decodeResult = decode(fetchResult)
            val executeResult = execute(decodeResult)
            val memoryAccessResult = memoryAccess(executeResult)
            val writeBackResult = writeBack(memoryAccessResult)

            logger.cycleCount(cycleCount)
            logger.fetchLog(cycleCount, fetchResult)
            logger.decodeLog(decodeResult)
            logger.executeLog(executeResult)
            logger.memoryAccessLog(controlSignal, memory, executeResult.aluResultValue)
            logger.writeBackLog(writeBackResult)
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
        this.controlSignal = decodeUnit.controlSignal(parsedInst.opcode)

        var writeRegister = mux(controlSignal.regDest, parsedInst.rd, parsedInst.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            opcode = parsedInst.opcode,
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

        var nextPc = mux(controlSignal.jump, decodeResult.address, registers.pc)
        nextPc = mux(aluResult.branchCondition, decodeResult.immediate, nextPc)
        nextPc = mux(controlSignal.jr, decodeResult.readData1, nextPc)

        return ExecutionResult(
            aluResultValue = aluResult.value,
            branchCondition = aluResult.branchCondition,
            memoryWriteData = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister,
            nextPc = nextPc
        )
    }

    private fun memoryAccess(executionResult: ExecutionResult): MemoryAccessResult {
        val readData = memory.read(
            memRead = controlSignal.memRead,
            address = executionResult.aluResultValue,
        )

        memory.write(
            address = executionResult.aluResultValue,
            value = executionResult.memoryWriteData,
            memWrite = controlSignal.memWrite
        )

        return MemoryAccessResult(
            readData = readData,
            aluResult = executionResult.aluResultValue,
            writeRegister = executionResult.writeRegister,
            nextPc = executionResult.nextPc
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        var writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluResult)
        writeData = mux(controlSignal.jal, registers.pc + 4, writeData)

        registers.pc = memoryAccessResult.nextPc

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )

        return WriteBackResult(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )
    }
}
