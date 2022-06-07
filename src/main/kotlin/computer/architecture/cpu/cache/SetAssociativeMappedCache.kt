package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger
import kotlin.math.pow

class SetAssociativeMappedCache(
    private val memory: Memory,
    private val offsetBits : Int = 4,
    private val indexBits: Int = 7,
    private val setBits : Int = 1,
) : ICache {
    private val addressBits = 32
    private val byteOffsetBits = 2

    private val tagBits = (addressBits - byteOffsetBits - offsetBits - indexBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    private val setSize = 2.0.pow(setBits).toInt()
    private val lineSizePerSet = 2.0.pow(indexBits).toInt()
    private val blockSize = 2.0.pow(offsetBits).toInt()

    private val lineSets = Array(setSize) {
        CacheLine.listOf(lineSizePerSet, blockSize)
    }

    override fun read(address: Int): Int {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        for (setIndex in 0 until setSize) {
            if (isHit(setIndex, tag, lineIndex)) {
                Logger.cacheHit()
                return lineSets[setIndex][lineIndex].datas[offset]
            }
        }

        Logger.cacheMiss()
        Logger.memoryFetch()
        for (setIndex in 0 until setSize) {
            if (!lineSets[setIndex][lineIndex].valid) {
                lineSets[setIndex][lineIndex].valid = true
                lineSets[setIndex][lineIndex].tag = tag
                lineSets[setIndex][lineIndex].datas = readLine(tag, lineIndex)
                return lineSets[setIndex][lineIndex].datas[offset]
            }
        }
        lineSets[0][lineIndex].valid = true
        lineSets[0][lineIndex].tag = tag
        lineSets[0][lineIndex].datas = readLine(tag, lineIndex)
        return lineSets[0][lineIndex].datas[offset]
    }

    override fun write(address: Int, value: Int) {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        for (setIndex in 0 until setSize) {
            if (isHit(setIndex, tag, lineIndex)) {
                lineSets[setIndex][lineIndex].datas[offset] = value
            }
        }
        Logger.memoryWrite()
        memory.write(address, value)
    }

    private fun readLine(tag: Int, lineIndex: Int) = Array(blockSize) {
        memory.read(address(tag, lineIndex, it))
    }

    private fun isHit(setIndex: Int, tag: Int, lineIndex: Int): Boolean {
        return lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag
    }

    private fun tag(address: Int) = address ushr (addressBits - tagBits)

    private fun index(address: Int) = (address shr byteOffsetBits shr offsetBits) % lineSizePerSet

    private fun offset(address: Int) = (address shr byteOffsetBits) % blockSize

    private fun address(tag: Int, index: Int, offset: Int): Int {
        return ((((tag shl indexBits) + index) shl offsetBits) + offset) shl byteOffsetBits + 0
    }
}
