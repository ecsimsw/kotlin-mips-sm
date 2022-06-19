package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.utils.Logger

class NoCachedMemory(
    val memory: Memory
) : ICache {

    override fun read(address: Int): Int {
        Logger.memoryRead()
        return memory.read(address)
    }

    override fun write(address: Int, value: Int) {
        Logger.memoryWrite()
        memory.write(address, value)
    }

    override fun flushAll() {
    }
}
