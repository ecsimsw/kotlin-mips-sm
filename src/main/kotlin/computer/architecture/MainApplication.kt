package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal

fun main() {
    initLoggingSignal()

    val instructionMemory = Memory(40000000)
    instructionMemory.loadFile("test_prog/input4.bin", 0)

    val controlUnit = ControlUnit(instructionMemory)
    controlUnit.process()
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        cycleLogging = true,
        fetchLogging = true,
        decodeLogging = true,
        executeLogging = true,
        memoryAccessLogging = true,
        writeBackLogging = true,
        finalValue = true,
        sleepTime = 0
    )
}
