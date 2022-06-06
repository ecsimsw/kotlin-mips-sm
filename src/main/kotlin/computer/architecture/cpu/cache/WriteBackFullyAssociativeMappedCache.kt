package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteBackFullyAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    lineBits: Int = 8
) : FullyAssociativeMappedCache(offsetBits, lineBits) {

    private val dirties = Array(lineCount) { false }
    private var oldestLineIndex = 0

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val offset = offset(address)
        val index = index(tag)

        if (index != -1) {
            dirties[index] = true
            cacheLines[index][offset] = value
        } else {
            Logger.memoryWrite()
            memory.write(address, value)
        }
    }

    override fun memoryFetch(tag: Int): Int {
        for (i in 0 until lineCount) {
            if (!valids[i]) {
                valids[i] = true
                tags[i] = tag
                cacheLines[i] = readBlockLine(tag)
                return i
            }

            if (valids[i] && tags[i] == tag) {
                return i
            }
        }

        updateDirties(oldestLineIndex)
        valids[oldestLineIndex] = true
        dirties[oldestLineIndex] = false
        tags[oldestLineIndex] = tag
        cacheLines[oldestLineIndex] = readBlockLine(tag)

        val fetchIndex = oldestLineIndex
        oldestLineIndex = (oldestLineIndex + 1) % lineCount
        return fetchIndex
    }

    private fun readBlockLine(tag: Int) = Array(blockCount) {
        Logger.memoryFetch()
        val address = address(tag, it)
        memory.read(address)
    }

    private fun updateDirties(index: Int) {
        if (dirties[index]) {
            Logger.memoryWrite()
            cacheLines[index].forEachIndexed { offset, data ->
                val address = address(tags[index], offset)
                memory.write(address, data)
            }
        }
    }
}
