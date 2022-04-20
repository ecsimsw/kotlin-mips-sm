package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal
import computer.architecture.utils.toHexString

/*
 covered : simple.bin
 */

fun main() {
    initLoggingSignal()

    val instructionMemory = Memory(16000000)
    instructionMemory.loadFile("test_prog/simple2.bin", 0)

    val controlUnit = ControlUnit(instructionMemory)
    controlUnit.process()
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        fetchLogging = true,
        decodeLogging = true,
        executeLogging = true,
        finalValue = true,
    )
}
