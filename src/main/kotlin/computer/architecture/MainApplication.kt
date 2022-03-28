package computer.architecture

import computer.architecture.cpu.ControlUnit
import computer.architecture.memory.Memory

fun main(args: Array<String>) {
    val executionFiles = mutableListOf(*args)
    if (executionFiles.isEmpty())
        executionFiles.add("sample/gcd.txt")

    for (path in executionFiles) {
        process(path)
    }
}

private fun process(path: String) {
    val memory = Memory(1000)
    val controlUnit = ControlUnit(memory)

    memory.loadFile(path, 0)
    controlUnit.process()
}
