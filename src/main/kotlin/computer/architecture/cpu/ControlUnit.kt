package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val pcControlUnit = PCControlUnit()
    private val alu = ALUnit()
    private var controlSignal = ControlSignal()

    fun process() {
        var cycleCount = 0
        while (registers.pc != (0xFFFFFFFF).toInt() && registers.pc < memory.size) {
            cycleCount++
            Logger.cycleCount(cycleCount)

            val fetchResult = fetch(registers.pc)
            Logger.fetchLog(fetchResult)

            val decodeResult = decode(fetchResult)
            Logger.decodeLog(decodeResult)

            val executeResult = execute(decodeResult)
            Logger.executeLog(executeResult, executeResult.nextPc)

            val memoryAccessResult = memoryAccess(executeResult)
            Logger.memoryAccessLog(controlSignal, executeResult.aluResult, memory[executeResult.aluResult])

            val writeBackResult = writeBack(memoryAccessResult)
            Logger.writeBackLog(writeBackResult)

            registers.pc = writeBackResult.nextPc
            Logger.sleep()
        }
        Logger.finalValue(registers[2])
    }

    private fun fetch(address: Int): FetchResult {
        val fetchResult = FetchResult(registers.pc, memory[address])
        registers.pc += 4
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val result = decodeUnit.decode(fetchResult.instruction)
        Logger.instructionDecode(result)

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

        val nextPc = pcControlUnit.next(
            pc = registers.pc,
            pcSrc1 = controlSignal.pcSrc1,
            pcSrc2 = controlSignal.pcSrc2 && !aluResult.isZero,
            pcSrc3 = controlSignal.pcSrc3,
            address = decodeResult.address,
            immediate = decodeResult.immediate,
            rsValue = decodeResult.readData1
        )

        return ExecutionResult(
            isZero = aluResult.isZero,
            aluResult = aluResult.resultValue,
            memoryWriteData = decodeResult.readData2,
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
            readData = readData,
            aluResult = executionResult.aluResult,
            writeRegister = executionResult.writeRegister,
            nextPc = executionResult.nextPc
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        var writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluResult)
        writeData = mux(controlSignal.jal, registers.pc+4, writeData)

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
