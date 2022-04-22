package computer.architecture.component

import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    size: Int,
) {
    private val memory: Array<Byte> = Array(size) { 0 }

    fun loadFile(path: String, address: Int = 0) {
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

    fun readInt(address: Int, memRead: Boolean = true): Int =
        if (memRead) {
            val i1 = memory[address] * 16777216 and 0xFF000000.toInt()
            val i2 = memory[address + 1] * 65536 and 0x00FF0000
            val i3 = memory[address + 2] * 256 and 0x0000FF00
            val i4 = memory[address + 3].toInt() and 0x000000FF
            i1 + i2 + i3 + i4
        } else 0

    fun writeInt(address: Int, value: Int, memWrite: Boolean = true) {
        if (memWrite) {
            memory[address] = (value shr 24 and 0xFF).toByte()
            memory[address + 1] = (value shr 16 and 0xFF).toByte()
            memory[address + 2] = (value shr 8 and 0xFF).toByte()
            memory[address + 3] = (value and 0xFF).toByte()
        }
    }
}
