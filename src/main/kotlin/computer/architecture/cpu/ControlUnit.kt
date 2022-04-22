package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
) {
    private val registers = Registers(32)
    private val logger = Logger(memory)
    private val decodeUnit = DecodeUnit()
    private val programCounterUnit = ProgramCounterUnit()
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

            logger.cycleCount(cycleCount - 1)
            logger.fetchLog(cycleCount - 1, fetchResult)
            logger.decodeLog(decodeResult)
            logger.executeLog(executeResult, executeResult.nextPc)
            logger.memoryAccessLog(controlSignal, executeResult.aluResult)
            logger.writeBackLog(writeBackResult)
        }
        return registers[2]
    }

    private fun fetch(address: Int): FetchResult {
        val fetchResult = FetchResult(registers.pc, memory.readInt(address))
        registers.pc += 4
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val idResult = decodeUnit.decode(fetchResult.instruction)
        controlSignal = ControlSignal(idResult.opcode)

        var writeRegister = mux(controlSignal.regDest, idResult.rd, idResult.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            opcode = idResult.opcode,
            shiftAmt = idResult.shiftAmt,
            immediate = idResult.immediate,
            address = idResult.address,
            readData1 = registers[idResult.rs],
            readData2 = registers[idResult.rt],
            writeRegister = writeRegister
        )
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        val aluResult = alu.operate(
            opcode = decodeResult.opcode,
            shiftAmt = decodeResult.shiftAmt,
            src1 = decodeResult.readData1,
            src2 = mux(controlSignal.aluSrc, decodeResult.immediate, decodeResult.readData2)
        )

        val nextPc = programCounterUnit.next(
            pc = registers.pc,
            jump = controlSignal.jType,
            branch = controlSignal.branch && !aluResult.isZero,
            jr = controlSignal.jr,
            address = decodeResult.address,
            immediate = decodeResult.immediate,
            rsValue = decodeResult.readData1
        )

        return ExecutionResult(
            aluResult = aluResult.resultValue,
            memoryWriteData = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister,
            nextPc = nextPc
        )
    }

    private fun memoryAccess(executionResult: ExecutionResult): MemoryAccessResult {
        val readData = memory.readInt(
            memRead = controlSignal.memRead,
            address = executionResult.aluResult,
        )

        memory.writeInt(
            address = executionResult.aluResult,
            value = executionResult.memoryWriteData,
            memWrite = controlSignal.memWrite
        )

        return MemoryAccessResult(
            readData = readData,
            aluResult = executionResult.aluResult,
            writeRegister = executionResult.writeRegister,
            nextPc = executionResult.nextPc
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        var writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluResult)
        writeData = mux(controlSignal.jal, registers.pc + 4, writeData)

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )

        registers.pc = memoryAccessResult.nextPc

        return WriteBackResult(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )
    }
}
