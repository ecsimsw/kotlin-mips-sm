package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy
import computer.architecture.utils.Logger

open class WriteThroughSetAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 7,
    setBits: Int = 1,
    replacementStrategy: CacheReplacementStrategy
) : AbstractAssociativeMappedCache(offsetBits, indexBits, setBits, replacementStrategy) {

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        Logger.memoryWrite()
        memory.write(address, value)

        val setIndex = setIndex(tag, lineIndex)
        if (setIndex != -1) {
            Logger.cacheHit()
            replacementStrategy.use(setIndex, lineIndex)
            lineSets[setIndex][lineIndex].datas[offset] = value
        } else {
            Logger.cacheMiss()
            memoryFetch(tag, lineIndex)
        }
    }

    override fun memoryFetch(tag: Int, lineIndex: Int): Int {
        for (setIndex in 0 until setSize) {
            if (lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag) {
                return setIndex
            }
            if (!lineSets[setIndex][lineIndex].valid) {
                lineSets[setIndex][lineIndex].fetch(tag, readBlockLine(tag, lineIndex))
                return setIndex
            }
        }
        val victimSet = replacementStrategy.nextVictim(lineIndex)
        lineSets[victimSet][lineIndex].fetch(tag, readBlockLine(tag, lineIndex))
        return victimSet
    }

    private fun readBlockLine(tag: Int, lineIndex: Int): Array<Int> {
        Logger.memoryFetch()
        return Array(blockSize) {
            val address = address(tag, lineIndex, it)
            memory.read(address)
        }
    }
}
