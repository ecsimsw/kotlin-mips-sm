package computer.architecture

import computer.architecture.cpu.ControlUnit
import computer.architecture.memory.Memory
import computer.architecture.memory.Results

fun main() {
    val memory = Memory.load("input/Von Neumann architecture.txt")
    val results = Results()

    val controlUnit = ControlUnit(memory, results)
    controlUnit.process()

    results.printAll()
}
