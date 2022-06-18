package computer.architecture.cpu.cache

import computer.architecture.component.Memory

class NoCachedMemory(
    val memory: Memory
) : ICache {

    override fun read(address: Int): Int {
        return memory.read(address)
    }

    override fun write(address: Int, value: Int) {
        memory.write(address, value)
    }
}
