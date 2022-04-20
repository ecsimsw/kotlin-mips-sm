package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger
import computer.architecture.utils.toHexString

class ControlUnit(
    private val memory: Memory,
) {
    private val registers = Registers(32)
    private val decodeUnit = DecodeUnit()
    private val pcControlUnit = PCControlUnit()
    private val alu = ALUnit()
    private var controlSignal = ControlSignal()

    fun process() {
        while (registers.pc != (0xFFFFFFFF / 4).toInt() && registers.pc < memory.size) {
            val fetchResult = fetch(registers.pc)
            Logger.fetchLog(fetchResult)

            val decodeResult = decode(fetchResult)
            Logger.decodeLog(decodeResult)

            val executeResult = execute(decodeResult)
            Logger.executeLog(executeResult)

            val memoryAccessResult = memoryAccess(executeResult)
            Logger.memoryAccessLog(memoryAccessResult)

            val writeBackResult = writeBack(memoryAccessResult)
            Logger.writeBackLog(writeBackResult)
        }
        Logger.finalValue(registers[2])
    }

    private fun fetch(address: Int): FetchResult {
        val fetchResult = FetchResult(registers.pc, memory[address])
        registers.pc++
        return fetchResult
    }

    private fun decode(fetchResult: FetchResult): DecodeResult {
        val result = decodeUnit.decode(fetchResult.instruction)
        Logger.instructionDecode(result)

        controlSignal = ControlSignal(result.opcode)

        return DecodeResult(
            opcode = result.opcode,
            shiftAmt = result.shiftAmt,
            immediate = result.immediate,
            address = result.address,
            readData1 = registers[result.rs],
            readData2 = registers[result.rt],
            writeRegister = mux(controlSignal.regWrite, result.rt, result.rd)
        )
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        println(decodeResult.immediate.toHexString())
        val aluResult = alu.operate(
            aluControl = ALUControl(controlSignal.aluOp, decodeResult.shiftAmt),
            src1 = decodeResult.readData1,
            src2 = mux(controlSignal.aluSrc, decodeResult.immediate, decodeResult.readData2)
        )

        val pcControlResult = pcControlUnit.jump(
            controlSignal = controlSignal,
            pc = registers.pc,
            readData1 = decodeResult.readData1
        )

        registers.pc = pcControlResult.pc

        return ExecutionResult(
            isZero = aluResult.isZero,
            aluResult = aluResult.resultValue,
            memoryWriteData = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister,
            nextPc = pcControlResult.pc
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
            writeRegister = executionResult.writeRegister
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult): WriteBackResult {
        val writeData = mux(controlSignal.memToReg, memoryAccessResult.readData, memoryAccessResult.aluResult)

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
