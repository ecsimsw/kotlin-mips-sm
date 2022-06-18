package computer.architecture.cpu.cache

import computer.architecture.component.Memory

class WriteBackDirectMappedCache(
    memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8
) : WriteBackSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits = indexBits,
    setBits = 0
)
