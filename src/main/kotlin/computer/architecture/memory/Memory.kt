package computer.architecture.memory

import java.io.File

class Memory(
    val size: Int
) {
    private val memory: Array<String> = Array(size) { "" }

    fun load(path: String, address:Int) {
        load(File(path).readLines(), address)
    }

    fun load(instructions: List<String>, address: Int) {
        var index = address
        instructions.forEach { memory[index++] = it }
    }

    fun store(address:Int, value:String) {
        memory[address] = value
    }

    fun read(address: Int): String {
        return memory[address]
    }
}
