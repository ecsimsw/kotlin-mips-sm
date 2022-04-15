package computer.architecture.single

import computer.architecture.single.Mux.Companion.mux
import computer.architecture.single.log.Logger

class ControlUnit(
    private val memory: Memory,
    private val registers: Registers = Registers(32),
) {
    private val controlSignal = ControlSignal()
    private val decodeUnit: DecodeUnit = DecodeUnit(controlSignal, registers)
    private val alu = ALU(controlSignal)

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            val decodeResult = decode(instruction)
            val executeResult = execute(decodeResult)
//            val memoryAccessResult = memoryAccess(executeResult)
//            val processResult = writeBack(memoryAccessResult)
//            storeLog(processResult)
        }
    }

    private fun fetch(address: Int): Int {
        val instruction = memory[address]
        registers.pc++
        Logger.fetchLog(registers.pc, instruction)
        return instruction
    }

    private fun decode(instruction: Int): DecodeResult {
        val decodeResult = decodeUnit.decode(instruction)
        Logger.decodeLog(decodeResult)
        return decodeResult
    }

    private fun execute(input: DecodeResult) {
        alu.operate(
            input.readData1,
            mux(controlSignal.aluSrc, input.address, input.readData2)
        )
        calculateAddress()
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
