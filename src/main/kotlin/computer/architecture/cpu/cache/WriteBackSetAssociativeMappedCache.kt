package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy
import computer.architecture.cpu.cache.replacement.RandomReplacementStrategy
import computer.architecture.utils.Logger

open class WriteBackSetAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 7,
    setBits: Int = 1,
    replacementStrategy: CacheReplacementStrategy = RandomReplacementStrategy()
) : AbstractAssociativeMappedCache(offsetBits, indexBits, setBits, replacementStrategy) {

    protected val dirties = Array(setSize) { Array(lineSize) { false } }

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        val setIndex = setIndex(tag, lineIndex)
        if (setIndex != -1) {
            Logger.cacheHit()
            replacementStrategy.use(setIndex, lineIndex)
            dirties[setIndex][lineIndex] = true
            lineSets[setIndex][lineIndex].datas[offset] = value
        } else {
            Logger.cacheMiss()
            val newSetIndex = memoryFetch(tag, lineIndex)
            dirties[newSetIndex][lineIndex] = true
            lineSets[newSetIndex][lineIndex].datas[offset] = value
        }
    }

    override fun memoryFetch(tag: Int, lineIndex: Int): Int {
        for (setIndex in 0 until setSize) {
            if (lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag) {
                return setIndex
            }

            if (!lineSets[setIndex][lineIndex].valid) {
                dirties[setIndex][lineIndex] = false
                lineSets[setIndex][lineIndex].fetch(tag, readBlockLine(tag, lineIndex))
                return setIndex
            }
        }

        val victimSet = replacementStrategy.nextVictim(lineIndex)
        updateDirties(victimSet, lineIndex)
        dirties[victimSet][lineIndex] = false
        lineSets[victimSet][lineIndex].fetch(tag, readBlockLine(tag, lineIndex))
        return victimSet
    }

    private fun updateDirties(setIndex: Int, lineIndex: Int) {
        if (dirties[setIndex][lineIndex]) {
            Logger.memoryWrite()
            lineSets[setIndex][lineIndex].datas.forEachIndexed { offset, data ->
                val tag = lineSets[setIndex][lineIndex].tag
                val address = address(tag, lineIndex, offset)
                memory.write(address, data)
            }
        }
    }

    private fun readBlockLine(tag: Int, lineIndex: Int): Array<Int> {
        Logger.memoryRead()
        return Array(blockSize) {
            val address = address(tag, lineIndex, it)
            memory.read(address)
        }
    }

    override fun flushAll() {
        for (setIndex in 0 until setSize) {
            dirties[setIndex].forEachIndexed { lineIndex, isDirty ->
                if (isDirty) {
                    Logger.memoryWrite()
                    lineSets[setIndex][lineIndex].datas.forEachIndexed { offset, data ->
                        val tag = lineSets[setIndex][lineIndex].tag
                        val address = address(tag, lineIndex, offset)
                        memory.write(address, data)
                        dirties[setIndex][lineIndex] = false
                    }
                }
            }
        }
    }
}
