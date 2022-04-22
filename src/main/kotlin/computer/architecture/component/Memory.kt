package computer.architecture.component

import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    val size: Int,
) {
    private val memory: Array<Byte> = Array(size) { 0 }

    fun loadFile(path: String, address: Int) {
        DataInputStream(FileInputStream(path)).use {
            val bytes = ByteArray(2048)
            var fromAddress = address
            while (it.read(bytes) > 0) {
                fromAddress = saveInstructions(bytes, fromAddress)
            }
        }
    }

    private fun saveInstructions(bytes: ByteArray, fromAddress: Int): Int {
        var address = fromAddress
        for (i in bytes.indices) {
            memory[address] = bytes[i]
            address++
        }
        return address
    }

    fun read(memRead: Boolean, address: Int): Byte =
        if (memRead) {
            memory[address]
        } else
            0

    fun write(memWrite: Boolean, address: Int, value: Byte) {
        if (memWrite) {
            memory[address] = value
        }
    }

    operator fun get(address: Int): Byte {
        return memory[address]
    }

    operator fun set(address: Int, value: Byte) {
        memory[address] = value
    }
}
