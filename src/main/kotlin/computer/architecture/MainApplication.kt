package computer.architecture

import computer.architecture.single.ControlUnit
import computer.architecture.single.MemoryBinary


fun main() {
    val memory = MemoryBinary(1000)
    val controlUnit = ControlUnit(memory)

    memory.loadFile("test_prog/simple.bin", 0)
    controlUnit.process()


//    Integer.decode()
//    val parseInt = Integer.parseInt("10101111101111100000000000000100", 2)
//    println("10101111101111100000000000000100".toLong(2));
}
