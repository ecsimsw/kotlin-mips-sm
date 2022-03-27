package computer.architecture

import computer.architecture.cpu.ControlUnit
import computer.architecture.cpu.Registers
import computer.architecture.memory.Memory
import computer.architecture.cpu.Results

fun main() {
    val memory = Memory(1000)
    memory.loadFile("input/gcd_recursive.txt", 0)

    val registers = Registers(10)
    val results = Results()

    val controlUnit = ControlUnit(memory, registers, results)
    controlUnit.process()

    results.printLogs()
    println("result value : ${registers.r[7]}")
}
