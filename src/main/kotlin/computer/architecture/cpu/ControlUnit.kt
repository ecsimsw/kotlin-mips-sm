package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers = Registers(32),
) {
    private val decodeUnit = DecodeUnit(registers)
    private val pcControlUnit = PCControlUnit()
    private val alu = ALUnit()
    private var controlSignal = ControlSignal(Opcode.SLL)

    fun process() {
        while (registers.pc != (0xFFFFFFFF/4).toInt() && registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            Logger.fetchLog(registers.pc, instruction)

            process(instruction)
            Logger.breakLine()
        }
    }

    fun process(instruction: Int) {
        val decodeResult = decode(instruction)
        Logger.decodeLog(decodeResult)

        val executeResult = execute(decodeResult)
        registers.pc = executeResult.nextPc
        Logger.executeLog(executeResult)

        val memoryAccessResult = memoryAccess(executeResult)
        writeBack(memoryAccessResult)
    }

    private fun fetch(address: Int): Int {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction: Int): DecodeResult {
        val decodeResult = decodeUnit.decode(instruction)
        controlSignal = decodeResult.controlSignal
        return decodeResult
    }

    private fun execute(decodeResult: DecodeResult): ExecutionResult {
        val aluResult = alu.operate(
            aluControl = ALUControl(controlSignal.aluOp, decodeResult.shiftAmt),
            src1 = decodeResult.readData1,
            src2 = mux(controlSignal.aluSrc, decodeResult.address, decodeResult.readData2)
        )

        val pcControlResult = pcControlUnit.jump(
            controlSignal = controlSignal,
            pc = registers.pc,
            readData1 = decodeResult.readData1
        )

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
            address = executionResult.aluResult,
            writeRegister = executionResult.writeRegister
        )
    }

    private fun writeBack(memoryAccessResult: MemoryAccessResult) {
        registers.write(
            regWrite = controlSignal.regWrite,
            writeRegister = memoryAccessResult.writeRegister,
            writeData = mux(controlSignal.memToReg, memoryAccessResult.address, memoryAccessResult.readData)
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
    val isZero: Boolean,
    val aluResult: Int,
    val memoryWriteData: Int,
    val writeRegister: Int,
    val nextPc: Int
)

data class MemoryAccessResult(
    val readData: Int,
    val address: Int,
    val writeRegister: Int,
)
