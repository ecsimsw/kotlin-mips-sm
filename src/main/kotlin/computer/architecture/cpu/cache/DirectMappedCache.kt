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

    protected val lineCount = 2.0.pow(indexBits).toInt()
    protected val blockCount = 2.0.pow(offsetBits).toInt()
    protected val valids = Array(lineCount) { false }
    protected val tags = Array(lineCount) { 0 }
    protected val cacheLines = Array(lineCount) { Array(blockCount) { 0 } }

    override fun read(address: Int): Int {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        return if (isHit(tag, index)) {
            Logger.cacheHit()
            cacheLines[index][offset]
        } else {
            Logger.cacheMiss()
            memoryFetch(tag, index)
            cacheLines[index][offset]
        }
    }

    abstract override fun write(address: Int, value: Int)

    abstract fun memoryFetch(tag: Int, index: Int)

    fun isHit(tag: Int, index: Int) = valids[index] && (tags[index] == tag)

    protected fun tag(address: Int) = address ushr (addressBits - tagBits)

    protected fun index(address: Int) = (address shr byteOffsetBits shr offsetBits) % lineCount

    protected fun offset(address: Int) = (address shr byteOffsetBits) % blockCount

    protected fun address(tag: Int, index: Int, offset: Int): Int {
        return ((((tag shl indexBits) + index) shl offsetBits) + offset) shl byteOffsetBits + 0
    }
}
