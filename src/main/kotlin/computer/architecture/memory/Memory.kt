package computer.architecture.memory

import java.io.File

class Memory(
    val size: Int
) {
    private val memory: Array<String> = Array(size) { "0x00" }

    fun loadFile(path: String, address: Int) {
        loadFile(File(path).readLines(), address)
    }

    fun loadFile(instructions: List<String>, address: Int) {
        var index = address
        instructions.forEach { memory[index++] = it }
    }

    operator fun get(address: Int): String {
        return memory[address]
    }

    operator fun set(address: Int, value: String) {
        memory[address] = value
    }
}
