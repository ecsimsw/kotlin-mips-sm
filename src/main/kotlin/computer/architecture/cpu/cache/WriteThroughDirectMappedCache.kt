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

        memory.write(address, value)
        if (isHit(tag, index)) {
            Logger.cacheHit()
            cacheLines[index][offset] = value
        } else {
            Logger.cacheMiss()
            memoryFetch(tag, index)
        }
    }

    override fun memoryFetch(tag: Int, index: Int) {
        Logger.cacheFetch()
        valids[index] = true
        tags[index] = tag
        cacheLines[index] = Array(blockCount) {
            val address = address(tag, index, it)
            memory.read(address)
        }
    }
}
