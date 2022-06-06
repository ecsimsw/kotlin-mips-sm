package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger
import computer.architecture.utils.toHexString

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

        if(address == 0xFF6544) {
            println("[write] FF6544 ${value}")
        }

        if (isHit(tag, index)) {
            Logger.cacheHit()
            dirties[index] = true
            Logger.log("[hit write] ${address.toHexString(8)} ${value}")
            cacheLines[index][offset] = value
        } else {
            Logger.cacheMiss()
            memoryFetch(tag, index)
            Logger.log("[miss write] ${address.toHexString(8)} ${value}")
            cacheLines[index][offset] = value
        }
    }

    override fun memoryFetch(tag: Int, index: Int) {
        Logger.cacheFetch()

        if (dirties[index]) {
            cacheLines[index].forEachIndexed { offset, data ->
                val address = address(tags[index], index, offset)
                Logger.log("[flush write] ${address.toHexString(8)} ${data}")
                memory.write(address, data)
            }
        }

        valids[index] = true
        dirties[index] = false
        tags[index] = tag
        cacheLines[index] = Array(blockCount) {
            val address = address(tag, index, it)
            Logger.log("[fetch] ${address.toHexString(8)} ${memory.read(address)}")
            memory.read(address)
        }
        Logger.log("")
    }
}
