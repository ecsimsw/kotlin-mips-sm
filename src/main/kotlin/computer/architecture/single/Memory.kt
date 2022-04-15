package computer.architecture.single

import computer.architecture.utils.toBinary
import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    val size: Int
) {
    private val memory: Array<Int> = Array(size) { 0 }

    fun loadFile(path: String, address: Int) {
        val bytes = ByteArray(1024)
        DataInputStream(FileInputStream(path)).use {
            if (it.read(bytes) > 0) {
                saveToMemory(bytes, address)
            }
        }
    }

    private fun saveToMemory(bytes: ByteArray, fromAddress: Int) {
        var saveAddress = fromAddress
        for (i in bytes.indices step (4)) {
            val binaryInstruction = bytes[i].toBinary(8) +
                    bytes[i + 1].toBinary(8) +
                    bytes[i + 2].toBinary(8) +
                    bytes[i + 3].toBinary(8)
            memory[saveAddress++] = binaryInstruction.toLong(2).toInt()
        }
    }

    fun read(memRead: Boolean, address: Int): Int {
        if (memRead) {
            return memory[address]
        }
        return 0
    }

    fun write(memWrite: Boolean, address: Int, value: Int) {
        if (memWrite) {
            memory[address] = value
        }
    }

    operator fun get(address: Int): Int {
        return memory[address]
    }

    operator fun set(address: Int, value: Int) {
        memory[address] = value
    }
}
