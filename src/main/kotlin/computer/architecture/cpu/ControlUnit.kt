package computer.architecture.cpu

import computer.architecture.memory.Memory

class ControlUnit(
    private val memory: Memory,
    registerSize: Int = 10
) {
    private val registers = Registers(registerSize)
    private val decodeUnit = DecodeUnit(registers)
    private val alu = ALU(registers, memory)
    private val results = Results(registers)

    fun process() {
        while (registers.pc < memory.size) {
            val instruction = fetch(registers.pc)
            val executionInfo = decode(instruction)
            execute(executionInfo)
            store(instruction)
        }
        printLogs()
    }

    private fun fetch(address: Int): String {
        val instruction = memory[address]
        registers.pc++
        return instruction
    }

    private fun decode(instruction: String): ExecutionInfo {
        return decodeUnit.decode(instruction)
    }

    private fun execute(executionInfo: ExecutionInfo) {
        alu.process(executionInfo)
    }

    private fun store(instruction: String) {
        results.saveLog(instruction)
    }

    private fun printLogs() {
        results.printLogs()
        results.printResultValue()
    }
}
