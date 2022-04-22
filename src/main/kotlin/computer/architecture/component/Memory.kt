package computer.architecture.component

import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    size: Int,
) {
    private val memory: Array<Byte> = Array(size) { 0 }

    fun loadFile(path: String) {
        DataInputStream(FileInputStream(path)).use {
            val bytes = ByteArray(2048)
            var address = 0
            while (it.read(bytes) > 0) {
                address = saveInstructions(bytes, address)
            }
        }
    }

    private fun saveInstructions(bytes: ByteArray, startAddress: Int): Int {
        var endAddress = startAddress
        bytes.forEach {
            memory[endAddress++] = it
        }
        return endAddress
    }

    fun readInt(address: Int, memRead: Boolean = true): Int =
        if (memRead) {
            val i1 = memory[address].toInt() shl 24
            val i2 = memory[address + 1].toInt() shl 16 and 0x00FF0000
            val i3 = memory[address + 2].toInt() shl 8 and 0x0000FF00
            val i4 = memory[address + 3].toInt() shl 0 and 0x000000FF
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
