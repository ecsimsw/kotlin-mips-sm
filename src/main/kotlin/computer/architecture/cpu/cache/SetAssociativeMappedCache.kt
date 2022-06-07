package computer.architecture.cpu.cache

import computer.architecture.utils.Logger
import kotlin.math.pow

abstract class SetAssociativeMappedCache(
    private val offsetBits: Int,
    private val indexBits: Int,
    private val setBits: Int,
) : ICache {
    private val addressBits = 32
    private val byteOffsetBits = 2
    private val tagBits = (addressBits - byteOffsetBits - offsetBits - indexBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    protected val setSize = 2.0.pow(setBits).toInt()
    protected val lineSize = 2.0.pow(indexBits).toInt()
    protected val blockSize = 2.0.pow(offsetBits).toInt()
    protected val lineSets = Array(setSize) { CacheLine.listOf(lineSize, blockSize) }

    override fun read(address: Int): Int {
        val tag = tag(address)
        val lineIndex = index(address)
        val offset = offset(address)

        var setIndex = setIndex(tag, lineIndex)
        return if(setIndex != -1) {
            Logger.cacheHit()
            lineSets[setIndex][lineIndex].datas[offset]
        } else {
            Logger.cacheMiss()
            setIndex = memoryFetch(tag, lineIndex)
            lineSets[setIndex][lineIndex].datas[offset]
        }
    }

    abstract fun memoryFetch(tag: Int, lineIndex: Int) : Int

    protected fun setIndex(tag: Int, lineIndex: Int): Int {
        for (setIndex in 0 until setSize) {
            if (lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag) {
                return setIndex
            }
        }
        return -1
    }

    protected fun tag(address: Int): Int {
        return address ushr (addressBits - tagBits)
    }

    protected fun index(address: Int): Int {
        return (address shr byteOffsetBits shr offsetBits) % lineSize
    }

    protected fun offset(address: Int): Int {
        return (address shr byteOffsetBits) % blockSize
    }

    protected fun address(tag: Int, index: Int, offset: Int): Int {
        return ((((tag shl indexBits) + index) shl offsetBits) + offset) shl byteOffsetBits + 0
    }
}
