package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteThroughDirectMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
) : DirectMappedCache(offsetBits, indexBits) {

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        if (isHit(tag, index)) {
            cacheLines[index].datas[offset] = value
        }

        Logger.memoryWrite()
        memory.write(address, value)
    }

    override fun memoryFetch(tag: Int, index: Int) {
        Logger.memoryFetch()
        cacheLines[index].valid = true
        cacheLines[index].tag = tag
        cacheLines[index].datas = Array(blockSize) {
            val address = address(tag, index, it)
            memory.read(address)
        }
    }
}
