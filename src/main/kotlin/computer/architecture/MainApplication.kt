package computer.architecture

import computer.architecture.cpu.ControlUnit
import computer.architecture.cpu.Registers
import computer.architecture.memory.Memory
import computer.architecture.memory.Results

fun main() {
    val memory = Memory(1000)
    memory.loadFile("input/gcd_recursive.txt", 0)

    val results = Results(100L)
    ControlUnit(memory, Registers(10), results).process()
//    results.printLogs()
}
