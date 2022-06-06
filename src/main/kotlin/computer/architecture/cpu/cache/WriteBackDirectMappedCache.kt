package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteBackDirectMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
) : DirectMappedCache(offsetBits, indexBits) {

    private val dirties = Array(linesCount) { false }

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        if (isHit(tag, index)) {
            Logger.cacheHit()
            dirties[index] = true
            cacheLines[index][offset] = value
        } else {
            Logger.cacheMiss()
            Logger.memoryWrite()
            memory.write(address, value)
        }
    }

    override fun memoryFetch(tag: Int, index: Int) {
        Logger.memoryFetch()

        if (dirties[index]) {
            Logger.memoryWrite()
            cacheLines[index].forEachIndexed { offset, data ->
                val address = address(tags[index], index, offset)
                memory.write(address, data)
            }
        }

        valids[index] = true
        dirties[index] = false
        tags[index] = tag
        cacheLines[index] = Array(blockCount) {
            val address = address(tag, index, it)
            memory.read(address)
        }
    }
}
