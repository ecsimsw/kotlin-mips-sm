package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal

fun main() {
    initLoggingSignal()

    val instructionMemory = Memory(20000000)
    instructionMemory.loadFile("test_prog/input4.bin")

    val controlUnit = ControlUnit(instructionMemory)
    val processResult = controlUnit.process()

    println("V0 : $processResult")
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        cycleLogging = true,
        fetchLogging = false,
        decodeLogging = false,
        executeLogging = false,
        memoryAccessLogging = false,
        writeBackLogging = false,
        finalValue = true,
        sleepTime = 0
    )
}
