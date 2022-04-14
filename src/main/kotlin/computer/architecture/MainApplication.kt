package computer.architecture

import computer.architecture.single.ControlUnit
import computer.architecture.single.MemoryBinary


fun main() {
    val memory = MemoryBinary(1000)
    val controlUnit = ControlUnit(memory)

    memory.loadFile("test_prog/simple.bin", 0)
    controlUnit.process()
}
