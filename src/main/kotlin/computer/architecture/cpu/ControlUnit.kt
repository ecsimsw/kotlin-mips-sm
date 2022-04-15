package computer.architecture.cpu

import computer.architecture.component.Memory
import computer.architecture.component.Mux.Companion.mux
import computer.architecture.component.Registers
import computer.architecture.utils.Logger

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers = Registers(32),
) {
    private val controlSignal = ControlSignal()
    private val du: DecodeUnit = DecodeUnit(controlSignal, registers)
    private val alu = ALUnit(controlSignal)

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            Logger.fetchLog(registers.pc, instruction)

            val decodeResult = decode(instruction)
            Logger.decodeLog(decodeResult)

            val executeResult = execute(decodeResult)
//            val memoryAccessResult = memoryAccess(executeResult)
//            val processResult = writeBack(memoryAccessResult)
//            storeLog(processResult)
        }
    }

    private fun fetch(address: Int): Int {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction: Int): DecodeResult {
        return du.decode(instruction)
    }

    private fun execute(decodeResult: DecodeResult) {
        val aluResult = alu.operate(
            src1 = decodeResult.readData1,
            src2 = mux(controlSignal.aluSrc, decodeResult.address, decodeResult.readData2)
        )
    }

    private fun calculateAddress() {
        TODO("Not yet implemented")
    }

    private fun memoryAccess(executeResult: Unit): Any {
        TODO("Not yet implemented")
    }

    private fun memoryOperate(decodeResult: DecodeResult) {
        TODO("Not yet implemented")
    }

    private fun writeRegister(decodeResult: DecodeResult) {
        TODO("Not yet implemented")
    }

    private fun storeLog(processResult: Any) {
        TODO("Not yet implemented")
    }

    private fun writeBack(memoryAccessResult: Any): Any {
        TODO("Not yet implemented")
    }
}
