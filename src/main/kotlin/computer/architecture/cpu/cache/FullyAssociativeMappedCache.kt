package computer.architecture.cpu.cache

import computer.architecture.utils.Logger
import kotlin.math.pow

abstract class FullyAssociativeMappedCache(
    private val offsetBits: Int,
    lineBits: Int
) : ICache {

    private val addressBits = 32
    private val byteOffsetBits = 2
    private val tagBits = (addressBits - byteOffsetBits - offsetBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    protected val lineCount = 2.0.pow(lineBits).toInt()
    protected val blockCount = 2.0.pow(offsetBits).toInt()

    protected val valids = Array(lineCount) { false }
    protected val tags = Array(lineCount) { 0 }
    protected val cacheLines = Array(lineCount) { Array(blockCount) { 0 } }

    override fun read(address: Int): Int {
        val tag = tag(address)
        val offset = offset(address)
        var index = index(tag)

        return if (index != -1) {
            Logger.cacheHit()
            cacheLines[index][offset]
        } else {
            Logger.cacheMiss()
            index = memoryFetch(tag)
            cacheLines[index][offset]
        }
    }

    abstract override fun write(address: Int, value: Int)

    abstract fun memoryFetch(tag: Int): Int

    fun tag(address: Int): Int {
        return address ushr (addressBits - tagBits)
    }

    fun offset(address: Int): Int {
        return (address shr byteOffsetBits) % blockCount
    }

    fun index(tag: Int): Int {
        for (i in 0 until lineCount) {
            if (valids[i] && tags[i] == tag) {
                return i
            }
        }
        return -1
    }

    fun address(tag: Int, offset: Int): Int {
        return ((tag shl offsetBits) + offset) shl byteOffsetBits + 0
    }
}
