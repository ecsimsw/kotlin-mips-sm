package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal
import computer.architecture.utils.toBinaryString

fun main() {
    initLoggingSignal()

    val instructionMemory = Memory(16000000)
    instructionMemory.loadFile("test_prog/simple.bin", 0)

    val controlUnit = ControlUnit(instructionMemory)
    controlUnit.process()
}

private fun testSingleInstruction(instructionMemory: Memory, instruction: String) {
    val controlUnit = ControlUnit(instructionMemory)
    controlUnit.processSingleInstruction(Integer.decode(instruction))
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        fetchLogging = true,
        decodeLogging = true
    )
}
