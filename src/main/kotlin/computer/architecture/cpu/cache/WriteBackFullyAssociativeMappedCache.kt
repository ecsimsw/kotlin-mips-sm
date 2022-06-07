package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteBackFullyAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    lineBits: Int = 8
) : FullyAssociativeMappedCache(offsetBits, lineBits) {

    private val dirties = Array(lineSize) { false }
    private var oldestLineIndex = 0

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val offset = offset(address)
        val index = index(tag)

        if (index != -1) {
            dirties[index] = true
            cacheLines[index].datas[offset] = value
        } else {
            Logger.memoryWrite()
            memory.write(address, value)
        }
    }

    override fun memoryFetch(tag: Int): Int {
        for (i in 0 until lineSize) {
            if (cacheLines[i].valid && cacheLines[i].tag == tag) {
                return i
            }

            if (!cacheLines[i].valid) {
                cacheLines[i].valid = true
                cacheLines[i].tag = tag
                cacheLines[i].datas = readBlockLine(tag)
                return i
            }
        }

        updateDirties(oldestLineIndex)
        cacheLines[oldestLineIndex].valid = true
        dirties[oldestLineIndex] = false
        cacheLines[oldestLineIndex].tag = tag
        cacheLines[oldestLineIndex].datas = readBlockLine(tag)

        val fetchIndex = oldestLineIndex
        oldestLineIndex = (oldestLineIndex + 1) % lineSize
        return fetchIndex
    }

    private fun readBlockLine(tag: Int): Array<Int> {
        Logger.memoryFetch()
        return Array(blockSize) {
            val address = address(tag, it)
            memory.read(address)
        }
    }

    private fun updateDirties(index: Int) {
        if (dirties[index]) {
            Logger.memoryWrite()
            cacheLines[index].datas.forEachIndexed { offset, data ->
                val tag = cacheLines[index].tag
                val address = address(tag, offset)
                memory.write(address, data)
            }
        }
    }
}
