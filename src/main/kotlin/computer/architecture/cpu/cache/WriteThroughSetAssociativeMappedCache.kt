package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class WriteThroughSetAssociativeMappedCache(
    private val memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 7,
    setBits: Int = 1,
) : SetAssociativeMappedCache(offsetBits, indexBits, setBits) {

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
        Logger.memoryFetch()

        val readLine = Array(blockSize) {
            memory.read(address(tag, lineIndex, it))
        }

        for (setIndex in 0 until setSize) {
            if (lineSets[setIndex][lineIndex].valid && lineSets[setIndex][lineIndex].tag == tag) {
                lineSets[setIndex][lineIndex].datas = readLine
                return setIndex
            }

            if (!lineSets[setIndex][lineIndex].valid) {
                lineSets[setIndex][lineIndex].valid = true
                lineSets[setIndex][lineIndex].tag = tag
                lineSets[setIndex][lineIndex].datas = readLine
                return setIndex
            }
        }

        lineSets[0][lineIndex].valid = true
        lineSets[0][lineIndex].tag = tag
        lineSets[0][lineIndex].datas = readLine
        return 0
    }
}
