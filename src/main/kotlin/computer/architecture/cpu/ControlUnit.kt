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

    fun process() {
        var cycleCount = 1
        while (registers.pc != (0xFFFFFFFF).toInt() && registers.pc < memory.size) {
            val fetchResult = fetch(registers.pc)
            val decodeResult = decode(fetchResult)
            val executeResult = execute(decodeResult)
            val memoryAccessResult = memoryAccess(executeResult)
            val writeBackResult = writeBack(memoryAccessResult)
            cycleCount++

            logger.cycleCount(cycleCount - 1)
            logger.fetchLog(cycleCount - 1, fetchResult)
            logger.decodeLog(decodeResult)
            logger.executeLog(executeResult, executeResult.nextPc)
            logger.memoryAccessLog(controlSignal, executeResult.aluResult)
            logger.writeBackLog(writeBackResult)
            logger.sleep()
        }
        logger.finalValue(registers[2])
    }

    private fun fetch(address: Int): FetchResult {
        val instruction = (memory[address].toInt() shl 24 and 0xFF000000.toInt()) +
                (memory[address + 1].toInt() shl 16 and 0x00FF0000) +
                (memory[address + 2].toInt() shl 8 and 0x0000FF00) +
                (memory[address + 3].toInt() and 0x000000FF)
        val fetchResult = FetchResult(registers.pc, instruction)
        registers.pc += 4
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val result = decodeUnit.decode(fetchResult.instruction)
        controlSignal = ControlSignal(result.opcode)

        var writeRegister = mux(controlSignal.regDest, result.rd, result.rt)
        writeRegister = mux(controlSignal.jal, 31, writeRegister)

        return DecodeResult(
            opcode = result.opcode,
            shiftAmt = result.shiftAmt,
            immediate = result.immediate,
            address = result.address,
            readData1 = registers[result.rs],
            readData2 = registers[result.rt],
            writeRegister = writeRegister
        )
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        val aluResult = alu.operate(
            aluControl = ALUControl(controlSignal.aluOp, decodeResult.shiftAmt),
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

        println(aluResult.resultValue)
        println(decodeResult.readData1)
        println(mux(controlSignal.aluSrc, decodeResult.immediate, decodeResult.readData2))
        println()

        return ExecutionResult(
            isZero = aluResult.isZero,
            aluResult = aluResult.resultValue,
            memoryWriteData = decodeResult.readData2.toByte(),
            writeRegister = decodeResult.writeRegister,
            nextPc = nextPc
        )
    }

    private fun memoryAccess(executionResult: ExecutionResult): MemoryAccessResult {
        val readData = memory.read(
            memRead = controlSignal.memRead,
            address = executionResult.aluResult,
        )

        memory.write(
            memWrite = controlSignal.memWrite,
            address = executionResult.aluResult,
            value = executionResult.memoryWriteData
        )

        return MemoryAccessResult(
            readData = readData.toInt(),
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
