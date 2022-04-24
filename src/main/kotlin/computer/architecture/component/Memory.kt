package computer.architecture.component

import java.io.DataInputStream
import java.io.FileInputStream

class Memory(
    size: Int,
    private val memory: Array<Byte> = Array(size) { 0 }
) {

    companion object {
        fun load(size: Int, path: String): Memory {
            DataInputStream(FileInputStream(path)).use { it ->
                val memory: Array<Byte> = Array(size) { 0 }
                val bytes = ByteArray(2048)
                var address = 0
                while (it.read(bytes) > 0) {
                    for(i : Int in bytes.indices step (4)) {
                        memory[address] = bytes[i+3]
                        memory[address+1] = bytes[i+2]
                        memory[address+2] = bytes[i+1]
                        memory[address+3] = bytes[i]
                        address+=4
                    }
                }
                return Memory(size, memory)
            }
        }
    }

    fun read(address: Int, memRead: Boolean = true): Int =
        if (memRead) {
            val i1 = memory[address].toInt() shl 0 and 0x000000FF
            val i2 = memory[address + 1].toInt() shl 8 and 0x0000FF00
            val i3 = memory[address + 2].toInt() shl 16 and 0x00FF0000
            val i4 = memory[address + 3].toInt() shl 24
            i4+ i3 + i2 + i1
        } else 0

    fun write(address: Int, value: Int, memWrite: Boolean = true) {
        if (memWrite) {
            memory[address] = (value and 0xFF).toByte()
            memory[address + 1] = (value shr 8 and 0xFF).toByte()
            memory[address + 2] = (value shr 16 and 0xFF).toByte()
            memory[address + 3] = (value shr 24 and 0xFF).toByte()
        }
    }
}
