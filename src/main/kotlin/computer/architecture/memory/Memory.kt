package computer.architecture.memory

import java.io.File

class Memory(
    private val instructions: List<String>
) {
    companion object {
        fun load(path: String): Memory {
            val instructions: MutableList<String> = arrayListOf();
            File(path).forEachLine { instructions.add(it) }
            return Memory(instructions)
        }
    }

    fun size(): Int {
        return instructions.size
    }

    fun readLine(index: Int): String {
        return instructions[index]
    }
}
