package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy
import computer.architecture.cpu.cache.replacement.LruReplacementStrategy
import computer.architecture.utils.Logger
import kotlin.math.pow

class WriteThroughSetAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 7,
    setBits: Int = 1,
    replacementStrategy: CacheReplacementStrategy = LruReplacementStrategy(
        setSize = 2.0.pow(setBits).toInt(),
        lineSize = 2.0.pow(indexBits).toInt()
    ),
) : SetAssociativeMappedCache(offsetBits, indexBits, setBits, replacementStrategy) {

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        val setIndex = setIndex(tag, lineIndex)
        if (setIndex != -1) {
            lineSets[setIndex][lineIndex].datas[offset] = value
        }
        Logger.memoryWrite()
        memory.write(address, value)
    }

    override fun memoryFetch(tag: Int, lineIndex: Int): Int {
        for (setIndex in 0 until setSize) {
            if (lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag) {
                return setIndex
            }

            if (!lineSets[setIndex][lineIndex].valid) {
                lineSets[setIndex][lineIndex].valid = true
                lineSets[setIndex][lineIndex].tag = tag
                lineSets[setIndex][lineIndex].datas = readBlockLine(tag, lineIndex)
                return setIndex
            }
        }

        val victimSet = replacementStrategy.nextVictim(lineIndex)
        lineSets[victimSet][lineIndex].valid = true
        lineSets[victimSet][lineIndex].tag = tag
        lineSets[victimSet][lineIndex].datas = readBlockLine(tag, lineIndex)
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
