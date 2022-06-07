package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteBackDirectMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
) : DirectMappedCache(offsetBits, indexBits) {

    private val dirties = Array(lineSize) { false }

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        if (isHit(tag, index)) {
            dirties[index] = true
            cacheLines[index].datas[offset] = value
        } else {
            Logger.memoryWrite()
            memory.write(address, value)
        }
    }

    override fun memoryFetch(tag: Int, index: Int) {
        Logger.memoryFetch()

        updateDirties(index)
        cacheLines[index].valid = true
        dirties[index] = false
        cacheLines[index].tag = tag
        cacheLines[index].datas = readBlockLine(tag, index)
    }

    private fun readBlockLine(tag: Int, index: Int) = Array(blockSize) {
        val address = address(tag, index, it)
        memory.read(address)
    }

    private fun updateDirties(index: Int) {
        if (dirties[index]) {
            Logger.memoryWrite()
            cacheLines[index].datas.forEachIndexed { offset, data ->
                val tag = cacheLines[index].tag
                val address = address(tag, index, offset)
                memory.write(address, data)
            }
        }
    }
}
