package computer.architecture.single

import java.io.DataInputStream
import java.io.FileInputStream

class MemoryBinary(
    val size: Int
) {
    private val memory: Array<String> = Array(size) { "00000000000000000000000000000000" }

    fun loadFile(path: String, address: Int) {
        val bytes = ByteArray(1024)
        DataInputStream(FileInputStream(path)).use {
            var index = address
            while (it.read(bytes) > 0) {
                for (i in bytes.indices step (4)) {
                    val bin1 = getBinary(bytes[i])
                    val bin2 = getBinary(bytes[i + 1])
                    val bin3 = getBinary(bytes[i + 2])
                    val bin4 = getBinary(bytes[i + 3])
                    memory[index++] = bin1 + bin2 + bin3 + bin4
                }
            }
        }
    }

    private fun getBinary(byte: Byte): String? {
        val hex = String.format("%02X", byte)
        val toBinaryString = Integer.toBinaryString(Integer.parseInt(hex, 16))
        return fillDigits(toBinaryString, 8)
    }

    private fun fillDigits(binary: String, count: Int): String? {
        var newBinary = binary
        if (newBinary.length < count) {
            for (i in 0 until count - newBinary.length) {
                newBinary = "0$newBinary"
            }
        }
        return newBinary
    }

    operator fun get(address: Int): String {
        return memory[address]
    }
}
