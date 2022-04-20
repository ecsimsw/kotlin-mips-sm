package computer.architecture.component

import computer.architecture.utils.Logger
import computer.architecture.utils.toBinary
import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    val size: Int,
) {
    private val memory: Array<Int> = Array(size) { 0 }

    fun loadFile(path: String, address: Int) {
        DataInputStream(FileInputStream(path)).use {
            val bytes = ByteArray(1024)
            if (it.read(bytes) > 0) {
                saveInstructions(bytes, address)
            }
        }
    }

    private fun saveInstructions(bytes: ByteArray, fromAddress: Int) {
        var address = fromAddress
        for (i in bytes.indices step (4)) {
            val binaryInstruction = bytes[i].toBinary(8) +
                    bytes[i + 1].toBinary(8) +
                    bytes[i + 2].toBinary(8) +
                    bytes[i + 3].toBinary(8)
            memory[address++] = binaryInstruction.toLong(2).toInt()
        }
    }

    fun read(memRead: Boolean, address: Int): Int {
        return if (memRead){
            memory[address/4]
        } else
            0
    }

    fun write(memWrite: Boolean, address: Int, value: Int) {
        if (memWrite) {
            memory[address/4] = value
        }
        Logger.memoryRead(address, memory[address/4])
    }

    operator fun get(address: Int): Int {
        return memory[address/4]
    }

    operator fun set(address: Int, value: Int) {
        memory[address/4] = value
    }
}
