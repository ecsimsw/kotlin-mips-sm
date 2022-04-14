package computer.architecture.cpu

import java.io.File

class Memory(
    val size: Int
) {
    private val memory: Array<String> = Array(size) { "0x00" }

    fun loadFile(path: String, address: Int) {
        loadFile(File(path).readLines(), address)
    }

    private fun loadFile(instructions: List<String>, address: Int) {
        var index = address
        instructions.forEach {
            println(it.toInt(2))
            memory[index++] = it
        }
    }

    operator fun get(address: Int): String {
        return memory[address]
    }

    operator fun set(address: Int, value: String) {
        memory[address] = value
    }

    operator fun set(address: Int, value: Int) {
        memory[address] = "0x" + Integer.toHexString(value).uppercase()
    }
}
