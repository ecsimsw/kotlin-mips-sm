package computer.architecture

import computer.architecture.single.ControlUnit
import computer.architecture.single.Memory
import computer.architecture.single.log.LoggingSignal

fun main() {
    initLoggingSignal()

    val memory = Memory(1000)
    val controlUnit = ControlUnit(memory)

    memory.loadFile("test_prog/simple.bin", 0)
    controlUnit.process()
}

private fun initLoggingSignal() {
    LoggingSignal.fetchLogging = true
    LoggingSignal.decodeLogging = true
}
