package computer.architecture.cpu.cache

import computer.architecture.utils.Logger
import kotlin.math.pow

abstract class DirectMappedCache(
    private val offsetBits: Int,
    private val indexBits: Int,
) : ICache {
    private val addressBits = 32
    private val byteOffsetBits = 2
    private val tagBits = (addressBits - byteOffsetBits - offsetBits - indexBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    protected val lineSize = 2.0.pow(indexBits).toInt()
    protected val blockSize = 2.0.pow(offsetBits).toInt()
    protected val cacheLines = CacheLine.listOf(lineSize, blockSize)

    override fun read(address: Int): Int {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        return if (isHit(tag, index)) {
            Logger.cacheHit()
            cacheLines[index].datas[offset]
        } else {
            Logger.cacheMiss()
            memoryFetch(tag, index)
            cacheLines[index].datas[offset]
        }
    }

    abstract fun memoryFetch(tag: Int, index: Int)

    fun isHit(tag: Int, index: Int): Boolean {
        return cacheLines[index].valid && (cacheLines[index].tag == tag)
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
