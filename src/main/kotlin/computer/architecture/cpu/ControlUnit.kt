package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers = Registers(32),
) {
    private val du: DecodeUnit = DecodeUnit(registers)
    private val alu = ALUnit()
    private var controlSignal = ControlSignal(Opcode.SLL)

    fun process() {
        while (registers.pc != (0xFFFFFFFF/4) || registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            Logger.fetchLog(registers.pc, instruction)

            val decodeResult = decode(instruction)
            Logger.decodeLog(decodeResult)

            val executeResult = execute(decodeResult)
            val memoryAccessResult = memoryAccess(executeResult)

            writeBack(memoryAccessResult)
        }
    }

    fun processSingleInstruction(instruction: Int) {
        Logger.fetchLog(registers.pc, instruction)

        val decodeResult = decode(instruction)
        Logger.decodeLog(decodeResult)

        val executeResult = execute(decodeResult)
        val memoryAccessResult = memoryAccess(executeResult)

        writeBack(memoryAccessResult)
    }

    private fun fetch(address: Int): Int {
        val instruction = memory[address]
        registers.pc ++
        return instruction
    }

    private fun decode(instruction: Int): DecodeResult {
        val decodeResult = du.decode(instruction)
        controlSignal = decodeResult.controlSignal
        return decodeResult
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        val src2 = mux(
            signal = controlSignal.aluSrc,
            trueResult = decodeResult.address,
            falseResult = decodeResult.readData2
        )

        val aluResult = alu.operate(
            aluControl = ALUControl(controlSignal.aluOp, decodeResult.shiftAmt),
            src1 = decodeResult.readData1,
            src2 = src2
        )

        return ExecutionResult(
            zero = aluResult.isZero,
            aluResult = aluResult.resultValue,
            readData2 = decodeResult.readData2,
            writeRegister = decodeResult.writeRegister
        )
    }

    private fun calculateAddress() {
        TODO("Not yet implemented")
    }

    private fun memoryAccess(executeResult: ExecutionResult): MemoryAccessResult {
        val readData = memory.read(
            memRead = controlSignal.memRead,
            address = executeResult.aluResult,
        )

        memory.write(
            memWrite = controlSignal.memWrite,
            address = executeResult.aluResult,
            value = executeResult.readData2
        )

        return MemoryAccessResult(
            readData = readData,
            address = executeResult.aluResult,
            writeRegister = executeResult.writeRegister
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult) {
        val writeData = mux(
            signal = controlSignal.memToReg,
            trueResult = memoryAccessResult.address,
            falseResult = memoryAccessResult.readData
        )

        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = writeData
        )
    }
}

data class DecodeResult(
    val opcode: Opcode,
    val shiftAmt: Int,
    val immediate: Int,
    val address: Int,
    val readData1: Int,
    val readData2: Int,
    val writeRegister: Int,
    val controlSignal: ControlSignal
)

data class ExecutionResult(
    val zero: Boolean,
    val aluResult: Int,
    val readData2: Int,
    val writeRegister: Int
)

data class MemoryAccessResult(
    val readData: Int,
    val address: Int,
    val writeRegister: Int,
)
