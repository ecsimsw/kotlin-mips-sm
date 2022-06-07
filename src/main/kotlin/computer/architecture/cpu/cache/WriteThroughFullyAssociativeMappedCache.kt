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
            cacheLines[index].datas[offset] = value
        }

        Logger.memoryWrite()
        memory.write(address, value)
    }

    override fun memoryFetch(tag: Int): Int {
        Logger.memoryFetch()

        val readLine = Array(blockSize) {
            val address = address(tag, it)
            memory.read(address)
        }

        for (i in 0 until lineSize) {
            if (cacheLines[i].valid && cacheLines[i].tag == tag) {
                cacheLines[i].datas = readLine
                return i
            }

            if (!cacheLines[i].valid) {
                cacheLines[i].valid = true
                cacheLines[i].tag = tag
                cacheLines[i].datas = readLine
                return i
            }
        }

        val fetchIndex = oldestLineIndex
        cacheLines[fetchIndex].valid = true
        cacheLines[fetchIndex].tag = tag
        cacheLines[fetchIndex].datas = readLine
        oldestLineIndex = (oldestLineIndex + 1) % lineSize
        return fetchIndex
    }
}
