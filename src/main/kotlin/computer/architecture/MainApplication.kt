package computer.architecture

import computer.architecture.component.Memory
import computer.architecture.cpu.ControlUnit
import computer.architecture.utils.LoggingSignal

fun main() {
    initLoggingSignal()

    val memory = Memory(1000)
    val controlUnit = ControlUnit(memory)

    memory.loadFile("test_prog/simple.bin", 0)
    controlUnit.process()
}

private fun initLoggingSignal() {
    LoggingSignal.init(
        fetchLogging = true,
        decodeLogging = true
    )
}
