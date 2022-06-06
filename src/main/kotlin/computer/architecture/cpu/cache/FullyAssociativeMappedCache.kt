package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger
import kotlin.math.pow

class FullyAssociativeMappedCache(
    private val memory: Memory,
    private val offsetBits: Int = 4,
    private val lineBits: Int = 8
) : ICache {

    private val addressBits = 32
    private val byteOffsetBits = 2
    private val tagBits = (addressBits - byteOffsetBits - offsetBits).also {
        if (it < 0) {
            throw IllegalArgumentException("tag bits can't be under 0")
        }
    }

    private val lineCount: Int = 2.0.pow(lineBits).toInt()
    private val blockCount = 2.0.pow(offsetBits).toInt()

    private val valids = Array(lineCount) { false }
    private val tags = Array(lineCount) { 0 }
    private val cacheLines = Array(lineCount) { Array(blockCount) { 0 } }

    private var oldestLineIndex = 0

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

    override fun write(address: Int, value: Int) {
        Logger.memoryWrite()
        memory.write(address, value)

        val tag = tag(address)
        val offset = offset(address)
        val index = index(tag)

        if (index != -1) {
            Logger.cacheHit()
            cacheLines[index][offset] = value
        } else {
            Logger.cacheMiss()
            memoryFetch(tag)
        }
    }

    private fun memoryFetch(tag: Int): Int {
        Logger.memoryFetch()

        val readLine = Array(blockCount) {
            val address = address(tag, it)
            memory.read(address)
        }

        for (i in 0..lineBits) {
            if (!valids[i]) {
                valids[i] = true
                tags[i] = tag
                cacheLines[i] = readLine
                return i
            }

            if (valids[i] && tags[i] == tag) {
                cacheLines[i] = readLine
                return i
            }
        }

        val fetchIndex = oldestLineIndex
        valids[fetchIndex] = true
        tags[fetchIndex] = tag
        cacheLines[fetchIndex] = readLine
        oldestLineIndex = (oldestLineIndex + 1) % lineCount
        return fetchIndex
    }

    fun tag(address: Int) = address ushr (addressBits - tagBits)

    fun offset(address: Int) = (address shr byteOffsetBits) % blockCount

    fun index(tag: Int): Int {
        for (i in 0..lineBits) {
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
