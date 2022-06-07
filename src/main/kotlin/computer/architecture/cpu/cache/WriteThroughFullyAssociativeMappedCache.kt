package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteThroughFullyAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    lineBits: Int = 8
) : FullyAssociativeMappedCache(offsetBits, lineBits) {

    private var oldestLineIndex = 0

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val index = index(tag)
        val offset = offset(address)

        if (index != -1) {
            cacheLines[index][offset] = value
        }

        Logger.memoryWrite()
        memory.write(address, value)
    }

    override fun memoryFetch(tag: Int): Int {
        Logger.memoryFetch()

        val readLine = Array(blockCount) {
            val address = address(tag, it)
            memory.read(address)
        }

        for (i in 0 until lineCount) {
            if (valids[i] && tags[i] == tag) {
                cacheLines[i] = readLine
                return i
            }

            if (!valids[i]) {
                valids[i] = true
                tags[i] = tag
                cacheLines[i] = readLine
                return i
            }
        }

        val fetchIndex = oldestLineIndex
        valids[fetchIndex] = true
        tags[fetchIndex] = tag
        cacheLines[fetchIndex] = readLine
        oldestLineIndex = (oldestLineIndex + 1) % lineCount
        return fetchIndex
    }
}
