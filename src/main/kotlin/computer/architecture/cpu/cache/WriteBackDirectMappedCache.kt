package computer.architecture.cpu.cache

import computer.architecture.component.Memory
import computer.architecture.cpu.cache.replacement.CacheReplacementStrategy

class WriteBackDirectMappedCache(
    memory: Memory,
    offsetBits: Int = 4,
    indexBits: Int = 8,
    replacementStrategy: CacheReplacementStrategy
) : WriteBackSetAssociativeMappedCache(
    memory = memory,
    offsetBits = offsetBits,
    indexBits = indexBits,
    setBits = 0,
    replacementStrategy = replacementStrategy
)
