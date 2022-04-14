package computer.architecture.single

import computer.architecture.utils.toBinaryString
import java.io.DataInputStream
import java.io.FileInputStream

class MemoryBinary(
    val size: Int
) {
    private val memory: Array<Int> = Array(size) { 0 }

    fun loadFile(path: String, address: Int) {
        val bytes = ByteArray(1024)
        DataInputStream(FileInputStream(path)).use {
            var index = address
            while (it.read(bytes) > 0) {
                for (i in bytes.indices step (4)) {
                    val bin1 = byteToBinary(bytes[i])
                    val bin2 = byteToBinary(bytes[i + 1])
                    val bin3 = byteToBinary(bytes[i + 2])
                    val bin4 = byteToBinary(bytes[i + 3])
                    memory[index++] = ((bin1 + bin2 + bin3 + bin4).toLong(2)).toInt()
                }
            }
        }
    }

    private fun byteToBinary(byte: Byte): String {
        val hexInt = Integer.parseInt(String.format("%02X", byte), 16)
        return hexInt.toBinaryString( 8)
    }

    operator fun get(address: Int): Int {
        return memory[address]
    }

    operator fun set(address: Int, value: Int) {
        memory[address] = value
    }
}
