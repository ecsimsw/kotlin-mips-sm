package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger
import kotlin.math.pow

class WriteBackDirectMappedCache(
    private val memory: Memory,
    private val offsetBits: Int = 4,
    private val indexBits: Int = 8,
) : ICache {
    private val addressBits = 32
    private val byteOffsetBits = 2
    private val tagBits = (addressBits - byteOffsetBits - offsetBits - indexBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    private val linesCount = 2.0.pow(indexBits).toInt()
    private val blockCount = 2.0.pow(offsetBits).toInt()

    private val dirties = Array(linesCount) { false }
    private val valids = Array(linesCount) { false }
    private val tags = Array(linesCount) { 0 }
    private val cacheLines = Array(linesCount) { Array(blockCount) { 0 } }

    override fun read(address: Int): Int {
        val tag = tag(address)
        val index = index(address)
        val offset = offset(address)

        if (isHit(tag, index)) {
            Logger.cacheHit()
            return cacheLines[index][offset]
        }

        Logger.cacheMiss()
        memoryFetch(tag, index)
        return cacheLines[index][offset]
    }

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
            memoryFetch(tag, index)
            cacheLines[index][offset] = value
        }
    }

    private fun isHit(tag: Int, index: Int): Boolean {
        return valids[index] && (tags[index] == tag)
    }

    private fun memoryFetch(tag: Int, index: Int) {
        Logger.cacheFetch()

        if (dirties[index]) {
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

    fun tag(address: Int) = address ushr (addressBits - tagBits)

    fun index(address: Int) = (address shr byteOffsetBits shr offsetBits) % linesCount

    fun offset(address: Int) = (address shr byteOffsetBits) % blockCount

    fun address(tag: Int, index: Int, offset: Int): Int {
        return ((((tag shl indexBits) + index) shl offsetBits) + offset) shl byteOffsetBits + 0
    }
}
